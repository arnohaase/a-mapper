package com.ajjpj.amapper.javabean.propbased

import com.ajjpj.amapper.javabean.{JavaBeanMappingHelper, SimpleJavaBeanObjectMappingDefBase}
import com.ajjpj.amapper.core.{QualifiedSourceAndTargetType, SimplePathSegment, PathBuilder, AMapperWorker}
import scala.reflect.ClassTag


/**
 * @author arno
 */
case class PropertyBasedObjectMappingDef[S<:AnyRef,T<:AnyRef](props: List[SourceAndTargetProp])(implicit sourceTag: ClassTag[S], targetTag: ClassTag[T]) extends SimpleJavaBeanObjectMappingDefBase[S,T] {
  override def doMap(source: S, target: T, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder) {
    props.foreach(p => {
      val isDeferred = p.sourceProp.isDeferred

      if(isDeferred) {
        worker.mapDeferred(path + SimplePathSegment(p.sourceProp.name), p.sourceProp.get(source), p.targetProp.get(target), p.types, v => p.targetProp.set(target, v))
      }
      else {
        worker.map(path + SimplePathSegment(p.sourceProp.name), p.sourceProp.get(source), p.targetProp.get(target), p.types, context) match {
          case Some(v) => p.targetProp.set(target, v)
          case _ =>
        }
      }
    })
  }
}

case class SourceAndTargetProp (sourceProp: PropertyAccessor, targetProp: PropertyAccessor) {
  def reverse = SourceAndTargetProp (targetProp, sourceProp)
  val types = QualifiedSourceAndTargetType (sourceProp.tpe, sourceProp.sourceQualifier, targetProp.tpe, targetProp.targetQualifier)
}
