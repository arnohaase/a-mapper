package com.ajjpj.amapper.collection

import com.ajjpj.amapper.core._
import com.ajjpj.amapper.core.ParameterizedPathSegment


/**
 * @author arno
 */
class EquivalenceBasedMerger[SE <: AnyRef, S<:Iterable[SE], T <: AMutableCollection[TE], TE<:AnyRef, H <: ACollectionHelper] {
  def map(source: S, target: T, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], context: Map[String, AnyRef], path: PathBuilder): T = {
    val h = worker.helpers

    val sourceElementType = worker.helpers.elementType(types.sourceType)
    val targetElementType = worker.helpers.elementType(types.targetType)

    val elementTypes = types.copy(sourceType = sourceElementType, targetType = targetElementType)

    val equiv = h.equivalenceMap(source, types.sourceType, target.asIterable, types.targetType)

    if(target.asIterable.isEmpty) // this is a performance optimization
      equiv.sourceWithoutTarget.foreach(s => target.add(worker.map(ACollectionSupport.elementPath(path, worker, s, sourceElementType), s, null, types, context).asInstanceOf[TE]))
    else {
      equiv.targetWithoutSource.foreach(target.remove)
      equiv.sourceWithoutTarget.map(s => target.add(worker.map(path + ParameterizedPathSegment("elements", worker.helpers.uniqueIdentifier(s, sourceElementType)), s, null, elementTypes, context).asInstanceOf[TE]))

      equiv.sourcesWithTargetEquivalent.foreach (s => equiv.targetEquivalents(s) match {
        case e if e.size == 1 && e.iterator.next().count == 1 => //TODO unapply for set with a single element
          worker.map(ACollectionSupport.elementPath(path, worker, s, sourceElementType), s, e.iterator.next(), elementTypes, context)
        case e => // special case: several existing equivalents in the target collection for the same source element
          worker.logger.severalExistingTargetsForSource(path.build, s)
          e.foreach (x => (1 to x.count).foreach {_ => target.remove(x.el)})
          target.add(worker.map(ACollectionSupport.elementPath(path, worker, s, sourceElementType), s, e.iterator.next(), elementTypes, context).asInstanceOf[TE])
      })
    }

    target
  }
}



