package com.ajjpj.amapper.javabean.propbased

import com.ajjpj.amapper.javabean.{JavaBeanMappingHelper, SimpleJavaBeanObjectMappingDefBase}
import com.ajjpj.amapper.core._
import scala.reflect.ClassTag
import com.ajjpj.amapper.core.SimplePathSegment
import scala.Some
import com.ajjpj.amapper.core.QualifiedSourceAndTargetType


/**
 * @author arno
 */
case class PropertyBasedObjectMappingDef[S<:AnyRef,T<:AnyRef](props: List[PartialMapping[S,T]])(implicit sourceTag: ClassTag[S], targetTag: ClassTag[T]) extends SimpleJavaBeanObjectMappingDefBase[S,T] {
  override def doMap(source: S, target: T, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder) {
    props.foreach(_.doMap(source, target, worker, context, path))
  }

  override def diff(diff: ADiffBuilder, sourceOld: S, sourceNew: S, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: JavaBeanMappingHelper], contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], path: PathBuilder, isDerived: Boolean) {
    props.foreach(_.doDiff(diff, sourceOld, sourceNew, worker, contextOld, contextNew, path, isDerived))
  }
}

trait PartialMapping[S<:AnyRef, T<:AnyRef] {
  def sourceName: String
  def targetName: String
  def doMap(source: S, target: T, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder): Unit
  def doDiff(diff: ADiffBuilder, sourceOld: S, sourceNew: S, worker: AMapperWorker[_ <: JavaBeanMappingHelper], contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], path: PathBuilder, isDerived: Boolean): Unit
}

case class SourceAndTargetProp[S<:AnyRef, T<:AnyRef] (sourceProp: PropertyAccessor, targetProp: PropertyAccessor) extends PartialMapping[S,T] {
  def reverse = SourceAndTargetProp[T,S] (targetProp, sourceProp)
  val types = QualifiedSourceAndTargetType (sourceProp.tpe, sourceProp.sourceQualifier, targetProp.tpe, targetProp.targetQualifier)

  override def sourceName = sourceProp.name
  override def targetName = targetProp.name

  private def newPath(path: PathBuilder, isSourceSide: Boolean) = path + SimplePathSegment(if(isSourceSide) sourceProp.name else targetProp.name)
  val isDeferred = sourceProp.isDeferred

  override def doMap(source: S, target: T, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder) {
    if(isDeferred) {
      worker.mapDeferred(newPath(path, isSourceSide=true), sourceProp.get(source), targetProp.get(target), types, v => targetProp.set(target, v))
    }
    else {
      worker.map(newPath(path, isSourceSide=true), sourceProp.get(source), targetProp.get(target), types, context) match {
        case Some(v) => targetProp.set(target, v)
        case _ =>
      }
    }
  }

  def doDiff(diff: ADiffBuilder, sourceOld: S, sourceNew: S, worker: AMapperWorker[_ <: JavaBeanMappingHelper], contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], path: PathBuilder, isDerived: Boolean) {
    if(isDeferred)
      worker.diffDeferred (newPath(path, isSourceSide=false), sourceProp.get(sourceOld), sourceProp.get(sourceNew), types, contextOld, contextNew, isDerived)
    else
      worker.diff (newPath(path, isSourceSide=false), sourceProp.get(sourceOld), sourceProp.get(sourceNew), types, contextOld, contextNew, isDerived)
  }
}


abstract class ExplicitPartialMapping[S<:AnyRef, T<:AnyRef] extends PartialMapping[S,T] {
  override val sourceName = ExplicitPartialMapping.uniqueName
  override val targetName = ExplicitPartialMapping.uniqueName
}

object ExplicitPartialMapping {
  private var number = 0
  private def nextNumber = {number += 1; number}

  def uniqueName = "synthetic-" + nextNumber
}


trait ShouldMap[S<:AnyRef,T<:AnyRef] {
  def shouldMap(source: S, target: T, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder): Boolean
}

class GuardedPartialMapping[S<:AnyRef,T<:AnyRef](inner: PartialMapping[S,T], shouldMap: ShouldMap[S,T]) extends PartialMapping[S,T] {
  def sourceName = inner.sourceName
  def targetName = inner.targetName

  def doMap(source: S, target: T, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder) {
    if(shouldMap.shouldMap(source, target, worker, context, path)) {
      inner.doMap(source, target, worker, context, path)
    }
  }

  def doDiff(diff: ADiffBuilder, sourceOld: S, sourceNew: S, worker: AMapperWorker[_ <: JavaBeanMappingHelper], contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], path: PathBuilder, isDerived: Boolean) {
    (shouldMap.shouldMap(sourceOld, null.asInstanceOf[T], worker, contextOld, path), shouldMap.shouldMap(sourceNew, null.asInstanceOf[T], worker, contextNew, path)) match {
      case (true, true) => inner.doDiff(diff, sourceOld, sourceNew, worker, contextOld, contextNew, path, isDerived)
      case (false, false) =>
      case _ => // TODO log diff mismatch
    }
  }
}

