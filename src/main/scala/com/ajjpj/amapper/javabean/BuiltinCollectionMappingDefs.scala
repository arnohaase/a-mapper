package com.ajjpj.amapper.javabean

import com.ajjpj.amapper.core._
import scala.collection.convert.Wrappers
import scala.reflect.ClassTag
import com.ajjpj.amapper.collection._
import com.ajjpj.amapper.core.ParameterizedPathSegment
import com.ajjpj.amapper.core.QualifiedSourceAndTargetType


/**
 * @author arno
 */
object BuiltinCollectionMappingDefs {
  def pathSegment(key: AnyRef) = ParameterizedPathSegment("elements", key)

  val MergingSetMappingDef = new EquivMergingSetCollectionMappingDef[juSet, JavaBeanMappingHelper with ACollectionHelper]
  val MergingListMappingDef: AObjectMappingDef[juCollection, juList, JavaBeanMappingHelper with ACollectionHelper] = new EquivMergingSetCollectionMappingDef[juList, JavaBeanMappingHelper with ACollectionHelper]
}


trait CanHandleCollection [TC <: juCollection] {
  val collectionType = JavaBeanTypes[juCollection]

  def targetType: SingleParamBeanType[TC, _<:AnyRef]

  def canHandle(types: QualifiedSourceAndTargetType) = (types.sourceType, types.targetType) match {
    case (stCandidate: JavaBeanType[_], ttCandidate: SingleParamBeanType[_, _]) => collectionType.isAssignableFrom(stCandidate) && targetType.isAssignableFrom(ttCandidate)
    case _ => false
  }
}


class EquivMergingSetCollectionMappingDef[TC <: juCollection,H<:JavaBeanMappingHelper with ACollectionHelper](implicit clsTag: ClassTag[TC])
  extends AObjectMappingDef[juCollection, TC,H]
  with CanHandleCollection[TC] {
  override def targetType = JavaBeanTypes[TC, AnyRef]

  val merger = new EquivalenceBasedMerger[AnyRef, Iterable[AnyRef], AMutableCollection[AnyRef], AnyRef, H] ()

  override def map(source: juCollection, targetRaw: TC, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], context: Map[String, AnyRef], path: PathBuilder): TC = {
    val target: TC = if (targetRaw != null) targetRaw else worker.helpers.createEmptyMutableCollection(types.targetType, types.sourceType).underlying.asInstanceOf[TC]

    merger.map(Wrappers.JCollectionWrapper(source), ACollectionAdapter(target), types, worker, context, path)

    target
  }

  override def diff(diff: ADiffBuilder, sourceOld: juCollection, sourceNew: juCollection, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], path: PathBuilder, isDerived: Boolean) {
    merger.diff(diff, Wrappers.JCollectionWrapper(sourceOld), Wrappers.JCollectionWrapper(sourceNew), types, worker, contextOld, contextNew, path, isDerived)
  }
}
