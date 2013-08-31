package com.ajjpj.amapper.core.impl

import com.ajjpj.amapper.core._
import scala.collection.mutable.ArrayBuffer

/**
 * @author arno
 */
private[impl] class AMapperWorkerImpl[H] (valueMappings: MappingDefResolver[AValueMappingDef[_,_, _ >: H]],
                                     objectMappings: MappingDefResolver[AObjectMappingDef[_,_, _ >: H]],
                                     val logger: AMapperLogger, deProxyStrategy: AnyRef => AnyRef,
                                     val helpers: H,
                                     deferredWork: ArrayBuffer[()=>Unit]) extends AMapperWorker[H] {
  private val identityCache = new IdentityCache

  override def map(path: PathBuilder, source: AnyRef, sourceType: AType, sourceQualifier: AQualifier, target: AnyRef, targetType: AType, targetQualifier: AQualifier) = {
    logger.debug ("map: " + sourceType + " @ " + path.build)
    valueMappings.mappingDefFor(sourceType, targetType, sourceQualifier, targetQualifier) match {
      case Some(m) =>
        mapValue(m, source, sourceType, sourceQualifier, targetType, targetQualifier)
      case None =>
        objectMappings.mappingDefFor(sourceType, targetType, sourceQualifier, targetQualifier) match {
          case Some(m) => mapObject(m, source, sourceType, sourceQualifier, target, targetType, targetQualifier, path)
          case None => throw new AMapperException("no mapping def found for " + sourceType + " / " + targetType + ".", path.build)
        }
    }
  }

  private def mapValue(v: AValueMappingDef[_,_,_ >: H], source: AnyRef, sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier) = if(source == null && ! v.handlesNull) null else v.asInstanceOf[AValueMappingDef[AnyRef, AnyRef, H]].map(source, sourceType, sourceQualifier, targetType, targetQualifier, this)

  private def mapObject(m: AObjectMappingDef[_,_,_ >: H], sourceRaw: AnyRef, sourceType: AType, sourceQualifier: AQualifier, target: AnyRef, targetType: AType, targetQualifier: AQualifier, path: PathBuilder) = {
    val source = deProxyStrategy(sourceRaw)
    val result = m.asInstanceOf[AObjectMappingDef[AnyRef, AnyRef, H]].map(source, sourceType, sourceQualifier, target, targetType, targetQualifier, this, path)
    identityCache.register(source, result, path)
    result
  }

  /**
   * Deferred mapping causes this mapping to be deferred until no non-deferred mapping work is left. The underlying
   *  abstraction is that there is a primary, hierarchical structure through which all objects can be
   *  reached, and a secondary structure of non-containment references. <p />
   * The former is mapped first so as to provide well-defined, 'normalized' paths to every element. Therefore, every
   *  object must be reachable by <em>exactly one</em> non-deferred path; ambiguities are not permitted and must be
   *  resolved by making one of the references 'deferred'.<p />
   * All objects must be reachable through the primary hierarchy. Put differently, it is invalid for
   *  an unmapped object to be reached through a deferred 'map' call. <p />
   * NB: deferred mapping automatically means mutable state in the target object structure because the result of the
   *  mapping is created after the initial object structure is complete. This also prevents streaming processing.
   */
  override def mapDeferred(path: PathBuilder, sourceRaw: AnyRef, sourceType: AType, sourceQualifier: AQualifier, target: => AnyRef, targetType: AType, targetQualifier: AQualifier, callback: (AnyRef) => Unit) {
    logger.debug ("map deferred: " + sourceType + " @ " + path.build)
    deferredWork += (() => {
      def doMap() = {
        logger.debug ("processing deferred: " + sourceType + " @ " + path.build)
        val source = deProxyStrategy(sourceRaw)
        identityCache.get(source) match {
          case Some(prevTarget) =>
            callback(prevTarget)
          case None =>
            logger.deferredWithoutInitial(path.build) //TODO special treatment for collections etc. --> flag in the mapping def?
            val mapped = map(path, source, sourceType, sourceQualifier, target, targetType, targetQualifier)
            callback(mapped)
        }
      }

      objectMappings.mappingDefFor(sourceType, targetType, sourceQualifier, targetQualifier) match {
        case Some(m) => doMap()
        case None => throw new AMapperException("no mapping def found for " + sourceType + " / " + targetType + ".", path.build)
      }
    })
  }
}
