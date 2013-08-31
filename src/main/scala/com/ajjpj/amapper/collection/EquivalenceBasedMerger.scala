package com.ajjpj.amapper.collection

import com.ajjpj.amapper.core._
import com.ajjpj.amapper.core.ParameterizedPathSegment


/**
 * @author arno
 */
class EquivalenceBasedMerger[SE <: AnyRef, S<:Iterable[SE], T <: AMutableCollection[TE], TE<:AnyRef, H <: ACollectionHelper] {
  def map(source: S, sourceType: AType, sourceQualifier: AQualifier, targetRaw: T, targetType: AType, targetQualifier: AQualifier, worker: AMapperWorker[_ <: H], context: Map[String, AnyRef], path: PathBuilder): T = {
    val h = worker.helpers

    val target = if (targetRaw != null) targetRaw else worker.helpers.createEmptyMutableCollection(targetType, sourceType).asInstanceOf[T]

    val sourceElementType = worker.helpers.elementType(sourceType)
    val targetElementType = worker.helpers.elementType(targetType)

    val equiv = h.equivalenceMap(source, sourceType, target.asIterable, targetType)

    if(target.asIterable.isEmpty) // this is a performance optimization
      equiv.sourceWithoutTarget.foreach(s => target.add(worker.map(ACollectionSupport.elementPath(path, worker, s, sourceElementType), s, sourceElementType, sourceQualifier, null, targetElementType, targetQualifier, context).asInstanceOf[TE]))
    else {
      equiv.targetWithoutSource.foreach(target.remove)
      equiv.sourceWithoutTarget.map(s => target.add(worker.map(path + ParameterizedPathSegment("elements", worker.helpers.uniqueIdentifier(s, sourceElementType)), s, sourceElementType, sourceQualifier, null, targetElementType, targetQualifier, context).asInstanceOf[TE]))

      equiv.sourcesWithTargetEquivalent.foreach (s => equiv.targetEquivalents(s) match {
        case e if e.size == 1 && e.iterator.next().count == 1 => //TODO unapply for set with a single element
          worker.map(ACollectionSupport.elementPath(path, worker, s, sourceElementType), s, sourceElementType, sourceQualifier, e.iterator.next(), targetElementType, targetQualifier, context)
        case e => // special case: several existing equivalents in the target collection for the same source element
          worker.logger.severalExistingTargetsForSource(path.build, s)
          e.foreach (x => (1 to x.count).foreach {_ => target.remove(x.el)})
          target.add(worker.map(ACollectionSupport.elementPath(path, worker, s, sourceElementType), s, sourceElementType, sourceQualifier, e.iterator.next(), targetElementType, targetQualifier, context).asInstanceOf[TE])
      })
    }

    target
  }
}



