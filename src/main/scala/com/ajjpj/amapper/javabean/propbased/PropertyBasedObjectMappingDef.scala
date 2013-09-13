package com.ajjpj.amapper.javabean.propbased

import com.ajjpj.amapper.javabean.{JavaBeanMappingHelper, SimpleJavaBeanObjectMappingDefBase}
import com.ajjpj.amapper.core.{QualifiedSourceAndTargetType, SimplePathSegment, PathBuilder, AMapperWorker}
import scala.reflect.ClassTag


/**
 * @author arno
 */
case class PropertyBasedObjectMappingDef[S<:AnyRef,T<:AnyRef](props: List[PartialMapping])(implicit sourceTag: ClassTag[S], targetTag: ClassTag[T]) extends SimpleJavaBeanObjectMappingDefBase[S,T] {
  override def doMap(source: S, target: T, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder) {
    props.foreach(_.doMap(source, target, worker, context, path))
  }
}

trait PartialMapping {
  def sourceName: String
  def targetName: String
  def doMap(source: AnyRef, target: AnyRef, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder): Unit
}

case class SourceAndTargetProp (sourceProp: PropertyAccessor, targetProp: PropertyAccessor) extends PartialMapping {
  def reverse = SourceAndTargetProp (targetProp, sourceProp)
  val types = QualifiedSourceAndTargetType (sourceProp.tpe, sourceProp.sourceQualifier, targetProp.tpe, targetProp.targetQualifier)

  override def sourceName = sourceProp.name
  override def targetName = targetProp.name

  override def doMap(source: AnyRef, target: AnyRef, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder) {
    val isDeferred = sourceProp.isDeferred

    if(isDeferred) {
      worker.mapDeferred(path + SimplePathSegment(sourceProp.name), sourceProp.get(source), targetProp.get(target), types, v => targetProp.set(target, v))
    }
    else {
      worker.map(path + SimplePathSegment(sourceProp.name), sourceProp.get(source), targetProp.get(target), types, context) match {
        case Some(v) => targetProp.set(target, v)
        case _ =>
      }
    }
  }
}


abstract class ExplicitPartialMapping extends PartialMapping {
  override val sourceName = ExplicitPartialMapping.uniqueName
  override val targetName = ExplicitPartialMapping.uniqueName
}

object ExplicitPartialMapping {
  private var number = 0
  private def nextNumber = {number += 1; number}

  def uniqueName = "synthetic-" + nextNumber
}


trait ShouldMap {
  def shouldMap(source: AnyRef, target: AnyRef, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder): Boolean
}

class GuardedPartialMapping(inner: PartialMapping, shouldMap: ShouldMap) extends PartialMapping {
  def sourceName = inner.sourceName
  def targetName = inner.targetName

  def doMap(source: AnyRef, target: AnyRef, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder) {
    if(shouldMap.shouldMap(source, target, worker, context, path)) {
      inner.doMap(source, target, worker, context, path)
    }
  }
}

