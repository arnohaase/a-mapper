package com.ajjpj.amapper.javabean

import com.ajjpj.amapper.core._
import scala.collection.convert.Wrappers
import scala.reflect.ClassTag
import com.ajjpj.amapper.collection.{ACollectionAdapter, ACollectionHelper, AMutableCollection, EquivalenceBasedMerger}


/**
 * @author arno
 */
object BuiltinCollectionMappingDefs {
  type juCollection = java.util.Collection[AnyRef]
  type juSet = java.util.Set[AnyRef]
  type juList = java.util.List[AnyRef]

  def pathSegment(key: AnyRef) = ParameterizedPathSegment("elements", key)

  val MergingSetMappingDef = new AbstractEquivMergingSetCollectionMappingDef[juSet, JavaBeanMappingHelper with ACollectionHelper] {}
  val MergingListMappingDef: AObjectMappingDef[juCollection, juList, JavaBeanMappingHelper with ACollectionHelper] = new AbstractEquivMergingSetCollectionMappingDef[juList, JavaBeanMappingHelper with ACollectionHelper] {}

  //------------------------

  abstract class AbstractCollectionMappingDef[TC <: juCollection, H <: JavaBeanMappingHelper](targetType: SingleParamBeanType[TC, _ <: AnyRef]) extends AObjectMappingDef[juCollection, TC, H] {
    val collectionType = JavaBeanTypes[juCollection]

    override def canHandle(types: QualifiedSourceAndTargetType) = (types.sourceType, types.targetType) match {
      case (stCandidate: JavaBeanType[_], ttCandidate: SingleParamBeanType[_, _]) => collectionType.isAssignableFrom(stCandidate) && targetType.isAssignableFrom(ttCandidate)
      case _ => false
    }

    final override def map(source: juCollection, target: TC, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], context: Map[String, AnyRef], path: PathBuilder) = {
      doMap(source, target, types, worker, context, path)
    }

    def doMap(source: juCollection, target: TC, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], context: Map[String, AnyRef], path: PathBuilder): TC

    def diff(diff: ADiffBuilder, sourceOld: BuiltinCollectionMappingDefs.juCollection, sourceNew: BuiltinCollectionMappingDefs.juCollection, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], path: PathBuilder, isDerived: Boolean) {
      //TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      throw new Error("TODO")
    }
  }

  //------------------------

  abstract class AbstractEquivMergingSetCollectionMappingDef[TC <: juCollection,H<:JavaBeanMappingHelper with ACollectionHelper](implicit clsTag: ClassTag[TC]) extends AbstractCollectionMappingDef[TC,H](JavaBeanTypes[TC, AnyRef]) {
    val merger = new EquivalenceBasedMerger[AnyRef, Iterable[AnyRef], AMutableCollection[AnyRef], AnyRef, H] ()

    override def doMap(source: juCollection, targetRaw: TC, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], context: Map[String, AnyRef], path: PathBuilder): TC = {
      val target: TC = if (targetRaw != null) targetRaw else worker.helpers.createEmptyMutableCollection(types.targetType, types.sourceType).underlying.asInstanceOf[TC]

      merger.map(Wrappers.JCollectionWrapper(source), ACollectionAdapter(target), types, worker, context, path)

      target
    }
  }
}
