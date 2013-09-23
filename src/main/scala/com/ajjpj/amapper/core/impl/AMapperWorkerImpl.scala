package com.ajjpj.amapper.core.impl

import com.ajjpj.amapper.core._
import scala.collection.mutable.ArrayBuffer

/**
 * @author arno
 */
private[impl] class AMapperWorkerImpl[H] (valueMappings: CanHandleSourceAndTargetCache[AValueMappingDef[_,_, _ >: H]],
                                     objectMappings: CanHandleSourceAndTargetCache[AObjectMappingDef[_,_, _ >: H]],
                                     val logger: AMapperLogger,
                                     val helpers: H, val identifierExtractor: IdentifierExtractor,
                                     contextExtractor: AContextExtractor, preProcessor: CanHandleSourceAndTargetCache[APreProcessor], postProcessor: CanHandleSourceAndTargetCache[APostProcessor],
                                     deferredWork: ArrayBuffer[()=>Unit]) extends AMapperWorker[H] {
  private val identityCache = new IdentityCache
  val diffBuilder = new ADiffBuilder

  override def map(path: PathBuilder, source: AnyRef, target: AnyRef, types: QualifiedSourceAndTargetType, context: Map[String, AnyRef]) = {
    valueMappings.entryFor(types) match {
      case Some(v) => Some(mapValue(path, source, types, context))
      case None => mapObject(path, source, target, types, context)
    }
  }

  override def mapValue(path: PathBuilder, source: AnyRef, types: QualifiedSourceAndTargetType, context: Map[String, AnyRef]) = {
    logger.debug ("map: " + source + " @ " + path.build)
    val m = valueMappingFor(types, path)
    m.asInstanceOf[AValueMappingDef[AnyRef, AnyRef, H]].map(source, types, this, context)
  }

  private def valueMappingFor(types: QualifiedSourceAndTargetType, path: PathBuilder) = valueMappings.entryFor(types) match {
    case Some(m) => m.asInstanceOf[AValueMappingDef[AnyRef, AnyRef, H]]
    case None => throw new AMapperException("no value mapping def found for " + types, path.build)
  }

  private def objectMappingFor(types: QualifiedSourceAndTargetType, path: PathBuilder) = objectMappings.entryFor(types) match {
    case Some(m) => m.asInstanceOf[AObjectMappingDef[AnyRef, AnyRef, H]]
    case None => throw new AMapperException("no object mapping def found for " + types, path.build)
  }


  override def mapObject(path: PathBuilder, sourceRaw: AnyRef, target: AnyRef, types: QualifiedSourceAndTargetType, context: Map[String, AnyRef]) = {
    logger.debug ("map: " + sourceRaw + " @ " + path.build)

    val m = objectMappingFor(types, path)
    val preProcessed = preProcessor
      .entryFor(types)
      .map(_.preProcess(sourceRaw, types)) // apply preprocessor (if any)
      .getOrElse(Some(sourceRaw)) // no preprocessor: leave 'as is'

    preProcessed.map(source => {
      val newContext = contextExtractor.withContext(context, sourceRaw, types.sourceType)
      val resultRaw = m.asInstanceOf[AObjectMappingDef[AnyRef, AnyRef, H]].map(source, target, types, this, newContext, path)

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
    })
  }

  override def diff(path: PathBuilder, sourceOld: AnyRef, sourceNew: AnyRef, types: QualifiedSourceAndTargetType, oldContext: Map[String, AnyRef], newContext: Map[String, AnyRef], isDerived: Boolean) {
    valueMappings.entryFor(types) match {
      case Some(v) => diffValue (path, sourceOld, sourceNew, types, oldContext, newContext, isDerived)
      case None    => diffObject(path, sourceOld, sourceNew, types, oldContext, newContext, isDerived)
    }
  }

  override def diffValue(path: PathBuilder, sourceOld: AnyRef, sourceNew: AnyRef, types: QualifiedSourceAndTargetType, oldContext: Map[String, AnyRef], newContext: Map[String, AnyRef], isDerived: Boolean) {
    logger.debug ("diff: " + sourceOld + " <-> " + sourceNew + " @ " + path.build)
    valueMappingFor(types, path).diff(diffBuilder, sourceOld, sourceNew, types, this, oldContext, newContext, path, isDerived)
  }

  override def diffObject(path: PathBuilder, sourceOldRaw: AnyRef, sourceNewRaw: AnyRef, types: QualifiedSourceAndTargetType, contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], isDerived: Boolean) {
    logger.debug ("diff: " + sourceOldRaw + " <-> " + sourceNewRaw + " @ " + path.build)
    val pre = preProcessor.entryFor(types)
    val preProcessedOld: Option[AnyRef] = pre
      .map(_.preProcess(sourceOldRaw, types)) // apply preprocessor (if any)
      .getOrElse(Some(sourceOldRaw)) // no preprocessor: leave 'as is'
    val preProcessedNew: Option[AnyRef] = pre
      .map(_.preProcess(sourceNewRaw, types)) // apply preprocessor (if any)
      .getOrElse(Some(sourceNewRaw)) // no preprocessor: leave 'as is'

    (preProcessedOld.isDefined, preProcessedNew.isDefined) match {
      case (false, false) =>
      case (true, true) =>
        val sourceOld = preProcessedOld.get
        val sourceNew = preProcessedNew.get

        doDiffObject(path, sourceOld, sourceNew, types, contextOld, contextNew, isDerived)
      case _ =>
        logger.diffPreProcessMismatch(path.build)
    }
  }

  private def doDiffObject(path: PathBuilder, sourceOld: AnyRef, sourceNew: AnyRef, types: QualifiedSourceAndTargetType, contextOldOrig: Map[String, AnyRef], contextNewOrig: Map[String, AnyRef], isDerived: Boolean) {
    val oldContext = contextExtractor.withContext (contextOldOrig, sourceOld, types.sourceType)
    val newContext = contextExtractor.withContext (contextNewOrig, sourceNew, types.sourceType)

    var causesDerived = false
    if(sourceOld == null && sourceNew != null) {
      diffBuilder.add (AddDiffElement (path.build, identifierExtractor.uniqueIdentifier(sourceNew, types.sourceType), isDerived))
      causesDerived = true
    }
    else if(sourceOld != null && sourceNew == null) {
      diffBuilder.add (RemoveDiffElement (path.build, identifierExtractor.uniqueIdentifier(sourceOld, types.sourceType), isDerived))
      causesDerived = true
    }
    else {
      val oldIdent = identifierExtractor.uniqueIdentifier (sourceOld, types.sourceType)
      val newIdent = identifierExtractor.uniqueIdentifier (sourceNew, types.sourceType)
      if (oldIdent != newIdent) {
        diffBuilder.add (ChangeRefDiffElement (path.build, oldIdent, newIdent, isDerived))
        causesDerived = true
      }
    }

    objectMappingFor(types, path).diff (diffBuilder, sourceOld, sourceNew, types, this, oldContext, newContext, path, isDerived || causesDerived)
    identityCache.register((sourceOld, sourceNew), this, path)
  }

  override def diffDeferred(path: PathBuilder, sourceOldRaw: AnyRef, sourceNewRaw: AnyRef, types: QualifiedSourceAndTargetType, contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], isDerived: Boolean) {
    logger.debug ("diff deferred: " + sourceOldRaw + " <-> " + sourceNewRaw + " @ " + path.build)
    deferredWork += (() => {
      logger.debug("processing deferred diff: " + types + "@" + path.build)

      val pre = preProcessor.entryFor(types)
      val preProcessedOld: Option[AnyRef] = pre
        .map(_.preProcess(sourceOldRaw, types)) // apply preprocessor (if any)
        .getOrElse(Some(sourceOldRaw)) // no preprocessor: leave 'as is'
      val preProcessedNew: Option[AnyRef] = pre
          .map(_.preProcess(sourceNewRaw, types)) // apply preprocessor (if any)
          .getOrElse(Some(sourceNewRaw)) // no preprocessor: leave 'as is'

      (preProcessedOld.isDefined, preProcessedNew.isDefined) match {
        case (false, false) =>
        case (true, true) if identityCache.get((preProcessedOld, preProcessedNew)).isDefined =>
        case (true, true) =>
          logger.deferredWithoutInitial(path.build)

          doDiffObject(path, preProcessedOld.get, preProcessedNew.get, types, contextOld, contextNew, isDerived)
        case _ =>
          logger.diffPreProcessMismatch(path.build)
      }
    })
  }
}
