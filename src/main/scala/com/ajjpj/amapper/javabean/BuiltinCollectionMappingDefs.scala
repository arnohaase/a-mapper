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

  abstract class AbstractEquivMergingSetCollectionMappingDef[TC <: juCollection,H<:JavaBeanMappingHelper with ACollectionHelper](implicit clsTag: ClassTag[TC]) extends AbstractCollectionMappingDef[TC,H](JavaBeanTypes[TC, AnyRef]) {
    val merger = new EquivalenceBasedMerger[AnyRef, Iterable[AnyRef], AMutableCollection[AnyRef], AnyRef, H] ()

    override def doMap(source: juCollection, sourceType: SingleParamBeanType[_<:AnyRef,_<:AnyRef], sourceQualifier: AQualifier, targetRaw: TC, targetType: SingleParamBeanType[TC,_<:AnyRef], targetQualifier: AQualifier, worker: AMapperWorker[_ <: H], path: PathBuilder): TC = {
      val target: TC = if (targetRaw != null) targetRaw else worker.helpers.createEmptyMutableCollection(targetType, sourceType).underlying.asInstanceOf[TC]

      merger.map(Wrappers.JCollectionWrapper(source), sourceType, sourceQualifier, ACollectionAdapter(target), targetType, targetQualifier, worker, path)

      target
    }
  }

  //------------------------

  abstract class AbstractCollectionMappingDef[TC <: juCollection, H <: JavaBeanMappingHelper](targetType: SingleParamBeanType[TC, _ <: AnyRef]) extends AObjectMappingDef[juCollection, TC, H] {
    val collectionType = JavaBeanTypes[juCollection]

    override def canHandle(st: AType, sourceQualifier: AQualifier, tt: AType, targetQualifier: AQualifier) = (st, tt) match {
      case (stCandidate: JavaBeanType[_], ttCandidate: SingleParamBeanType[_, _]) => collectionType.isAssignableFrom(stCandidate) && targetType.isAssignableFrom(ttCandidate)
      case _ => false
    }

    final override def map(source: juCollection, sourceTypeRaw: AType, sourceQualifier: AQualifier, target: TC, targetTypeRaw: AType, targetQualifier: AQualifier, worker: AMapperWorker[_ <: H], path: PathBuilder) = {
      val sourceType = sourceTypeRaw.asInstanceOf[SingleParamBeanType[_ <: AnyRef,_ <: AnyRef]]
      val targetType = targetTypeRaw.asInstanceOf[SingleParamBeanType[TC,_ <: AnyRef]]

      doMap(source, sourceType, sourceQualifier, target, targetType, targetQualifier, worker, path)
    }

    def doMap(source: juCollection, sourceType: SingleParamBeanType[_<:AnyRef,_<:AnyRef], sourceQualifier: AQualifier, target: TC, targetType: SingleParamBeanType[TC,_<:AnyRef], targetQualifier: AQualifier, worker: AMapperWorker[_ <: H], path: PathBuilder): TC
  }
}
