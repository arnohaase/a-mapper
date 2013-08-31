package com.ajjpj.amapper.javabean.propbased

import com.ajjpj.amapper.javabean.{JavaBeanMappingHelper, SimpleJavaBeanObjectMappingDefBase}
import com.ajjpj.amapper.core.{SimplePathSegment, PathBuilder, AMapperWorker}
import scala.reflect.ClassTag


/**
 * @author arno
 */
case class PropertyBasedObjectMappingDef[S<:AnyRef,T<:AnyRef](props: List[SourceAndTargetProp])(implicit sourceTag: ClassTag[S], targetTag: ClassTag[T]) extends SimpleJavaBeanObjectMappingDefBase[S,T] {
  def doMap(source: S, target: T, worker: AMapperWorker[_ <: JavaBeanMappingHelper], path: PathBuilder) {
    props.foreach(p => {
      val isDeferred = p.sourceProp.isDeferred

      if(isDeferred) {
        worker.mapDeferred(path + SimplePathSegment(p.sourceProp.name), p.sourceProp.get(source), p.sourceProp.tpe, p.sourceProp.sourceQualifier, p.targetProp.get(target), p.targetProp.tpe, p.targetProp.targetQualifier, v => p.targetProp.set(target, v))
      }
      else {
        val v = worker.map(path + SimplePathSegment(p.sourceProp.name), p.sourceProp.get(source), p.sourceProp.tpe, p.sourceProp.sourceQualifier, p.targetProp.get(target), p.targetProp.tpe, p.targetProp.targetQualifier)
        p.targetProp.set(target, v)
      }
    })
  }
}

case class SourceAndTargetProp (sourceProp: PropertyAccessor, targetProp: PropertyAccessor) {
  def reverse = SourceAndTargetProp (targetProp, sourceProp)
}
