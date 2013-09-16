package com.ajjpj.amapper.collection

import com.ajjpj.amapper.core._


/**
 * @author arno
 */
class EquivalenceBasedMerger[SE <: AnyRef, S<:Iterable[SE], T <: AMutableCollection[TE], TE<:AnyRef, H <: ACollectionHelper] {
  def map(source: S, target: T, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], context: Map[String, AnyRef], path: PathBuilder): T = {
    val h = worker.helpers

    val sourceElementType = worker.helpers.elementType(types.sourceType)
    val targetElementType = worker.helpers.elementType(types.targetType)
    val elementTypes = types.copy(sourceType = sourceElementType, targetType = targetElementType)

    val equiv = h.equivalenceMap(source, types.sourceType, target.asIterable, types.targetType, worker.identifierExtractor)

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

  def diff(diff: ADiffBuilder, source1: S, source2: S, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], path: PathBuilder, isDerived: Boolean) {
    val h = worker.helpers

    val sourceElementType = worker.helpers.elementType(types.sourceType)
    val targetElementType = worker.helpers.elementType(types.targetType)
    val elementTypes = types.copy(sourceType = sourceElementType, targetType = targetElementType)

    val equiv = h.equivalenceMap(source1, types.sourceType, source2, types.sourceType, worker.identifierExtractor)

    // elements present in both old and new collection: no difference as far as the collection is concerned, recursive diff
    equiv.sourcesWithTargetEquivalent.foreach (oldElement => {
      val newElement = equiv.targetEquivalents(oldElement).iterator.next().el //TODO deal with non-unique equivalents
      worker.diff(ACollectionSupport.elementPath (path, worker, oldElement, sourceElementType), oldElement, newElement, elementTypes, contextOld, contextNew, isDerived)
    })

    // elements only in the new collection: 'added' diff element + recursive diff with 'derived' = true
    equiv.targetWithoutSource.foreach (newElement => {
      val newPathBuilder = ACollectionSupport.elementPath(path, worker, newElement, sourceElementType)
//      diff.add(AddDiffElement(newPathBuilder.build, newElement, isDerived))
      worker.diff(newPathBuilder, null, newElement, elementTypes, contextOld, contextNew, isDerived)
    })

    // elements only in the old collection: 'removed' diff element + recursive diff with 'derived' = true
    equiv.sourceWithoutTarget.foreach (oldElement => {
      val newPathBuilder = ACollectionSupport.elementPath(path, worker, oldElement, sourceElementType)
      //      diff.add(AddDiffElement(newPathBuilder.build, newElement, isDerived))
      worker.diff(newPathBuilder, oldElement, null, elementTypes, contextOld, contextNew, isDerived)
    })
  }

  private def addNewElements (equiv: EquivalenceMap[SE, TE], worker: AMapperWorker[_ <: H], path: PathBuilder, elementTypes: QualifiedSourceAndTargetType, target: T, context: Map[String, AnyRef]) {
    val mapped = equiv.sourceWithoutTarget.flatMap(s => worker.map(ACollectionSupport.elementPath(path, worker, s, elementTypes.sourceType), s, null, elementTypes, context))
    mapped.foreach(t => target.add(t.asInstanceOf[TE]))
  }
}



