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

    if(target.asIterable.isEmpty)  // this is a performance optimization
      addNewElements(equiv, worker, path, elementTypes, target, context)
    else {
      equiv.targetWithoutSource.foreach(target.remove)
      addNewElements(equiv, worker, path, elementTypes, target, context)

      equiv.sourcesWithTargetEquivalent.foreach (s => equiv.targetEquivalents(s) match {
        case e if e.size == 1 && e.iterator.next().count == 1 => //TODO unapply for set with a single element
          worker.map(ACollectionSupport.elementPath(path, worker, s, sourceElementType), s, e.iterator.next(), elementTypes, context)
        case e => // special case: several existing equivalents in the target collection for the same source element
          worker.logger.severalExistingTargetsForSource(path.build, s)
          e.foreach (x => (1 to x.count).foreach {_ => target.remove(x.el)})

          worker.map(ACollectionSupport.elementPath(path, worker, s, sourceElementType), s, e.iterator.next(), elementTypes, context) match {
            case Some(o) => target.add(o.asInstanceOf[TE])
            case None =>
          }
      })
    }

    target
  }

  private def addNewElements (equiv: EquivalenceMap[SE, TE], worker: AMapperWorker[_ <: H], path: PathBuilder, elementTypes: QualifiedSourceAndTargetType, target: T, context: Map[String, AnyRef]) {
    val mapped = equiv.sourceWithoutTarget.flatMap(s => worker.map(ACollectionSupport.elementPath(path, worker, s, elementTypes.sourceType), s, null, elementTypes, context))
    mapped.foreach(t => target.add(t.asInstanceOf[TE]))
  }
}



