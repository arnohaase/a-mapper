package com.ajjpj.amapper.core.impl

import com.ajjpj.amapper.core._
import scala.collection.mutable.ArrayBuffer

/**
 * @author arno
 */
private[impl] class AMapperWorkerImpl[H] (valueMappings: CanHandleSourceAndTargetCache[AValueMappingDef[_,_, _ >: H]],
                                     objectMappings: CanHandleSourceAndTargetCache[AObjectMappingDef[_,_, _ >: H]],
                                     val logger: AMapperLogger,
                                     val helpers: H,
                                     contextExtractor: AContextExtractor, preProcessor: CanHandleSourceAndTargetCache[APreProcessor], postProcessor: CanHandleSourceAndTargetCache[APostProcessor],
                                     deferredWork: ArrayBuffer[()=>Unit]) extends AMapperWorker[H] {
  private val identityCache = new IdentityCache

  override def map(path: PathBuilder, source: AnyRef, target: AnyRef, types: QualifiedSourceAndTargetType, context: Map[String, AnyRef]) = {
    valueMappings.entryFor(types) match {
      case Some(v) => Some(mapValue(path, source, types, context))
      case None => mapObject(path, source, target, types, context)
    }
  }

  override def mapValue(path: PathBuilder, source: AnyRef, types: QualifiedSourceAndTargetType, context: Map[String, AnyRef]) = {
    logger.debug ("map: " + source + " @ " + path.build)
    valueMappings.entryFor(types) match {
      case Some(m) => if(source == null && ! m.handlesNull) null else m.asInstanceOf[AValueMappingDef[AnyRef, AnyRef, H]].map(source, types, this, context)
      case None => throw new AMapperException("no value mapping def found for " + types, path.build)
    }
  }

  override def mapObject(path: PathBuilder, source: AnyRef, target: AnyRef, types: QualifiedSourceAndTargetType, context: Map[String, AnyRef]) = {
    logger.debug ("map: " + source + " @ " + path.build)
    val newContext = contextExtractor.withContext(context, source, types.sourceType)

    objectMappings.entryFor(types) match {
      case Some(m) => doMapObject(m, source, target, types, newContext, path)
      case None => throw new AMapperException("no mapping def found for " + types + ".", path.build)
    }
  }

  private def doMapObject(m: AObjectMappingDef[_,_,_ >: H], sourceRaw: AnyRef, target: AnyRef, types: QualifiedSourceAndTargetType, context: Map[String, AnyRef], path: PathBuilder): Option[AnyRef] = {
    val preProcessed = preProcessor
      .entryFor(types)
      .map(_.preProcess(sourceRaw, types)) // apply preprocessor (if any)
      .getOrElse(Some(sourceRaw)) // no preprocessor: leave 'as is'

    preProcessed.map(source => {
      val resultRaw = m.asInstanceOf[AObjectMappingDef[AnyRef, AnyRef, H]].map(source, target, types, this, context, path)

      val result = postProcessor
        .entryFor(types)
        .map(_.postProcess(resultRaw, types))
        .getOrElse (resultRaw)

      if(m.isCacheable)
        identityCache.register(source, result, path)

      result
    })
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
  override def mapDeferred(path: PathBuilder, sourceRaw: AnyRef, target: => AnyRef, types: QualifiedSourceAndTargetType, callback: (AnyRef) => Unit) {
    logger.debug ("map deferred: " + types + " @ " + path.build)
    deferredWork += (() => {
      objectMappings.entryFor(types) match {
        case Some(m) =>
          logger.debug("processing deferred: " + types + " @ " + path.build)
          val source = sourceRaw //TODO deProxyStrategy(sourceRaw)
          identityCache.get(source) match {
            case Some(prevTarget) =>
              callback(prevTarget)
            case None =>
              logger.deferredWithoutInitial(path.build) //TODO special treatment for collections etc. --> flag in the mapping def?
            // create a new, empty context: context is accumulated only from parents to children
            mapObject(path, source, target, types, Map[String, AnyRef]()) match {
              case Some(mapped) => callback(mapped)
              case _ =>
            }
          }
        case None => throw new AMapperException("no mapping def found for " + types + ".", path.build)
      }
    })
  }
}
