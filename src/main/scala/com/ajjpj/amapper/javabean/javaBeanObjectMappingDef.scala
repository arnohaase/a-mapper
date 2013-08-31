package com.ajjpj.amapper.javabean

import com.ajjpj.amapper.core._
import scala.reflect.ClassTag

/**
 * @author arno
 */
abstract class AbstractJavaBeanObjectMappingDef[S<:AnyRef,T<:AnyRef](implicit sourceClassTag: ClassTag[S], targetClassTag: ClassTag[T])
    extends AObjectMappingDef[S,T,JavaBeanMappingHelper] {
  val sourceClass = sourceClassTag.runtimeClass.asInstanceOf[Class[S]]
  val targetClass = targetClassTag.runtimeClass.asInstanceOf[Class[T]]

  override def canHandle(sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier) = (sourceType, targetType) match {
    case (jst: JavaBeanType[_], jtt: JavaBeanType[_]) => sourceClass == jst.cls && targetClass == jtt.cls
    case _ => false
  }
}

abstract class SimpleJavaBeanObjectMappingDefBase[S<:AnyRef,T<:AnyRef](handlesSubclasses: Boolean = false)(implicit sourceClassTag: ClassTag[S], targetClassTag: ClassTag[T])
    extends AbstractJavaBeanObjectMappingDef[S,T] {
  override def map(source: S, sourceType: AType, sourceQualifier: AQualifier, targetRaw: T, targetType: AType, targetQualifier: AQualifier, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder): T = {
    if(source == null)
      null.asInstanceOf[T]
    else {
      val target = if (targetRaw != null) targetRaw else worker.helpers.createInstance(targetType.asInstanceOf[JavaBeanType[T]], sourceType.asInstanceOf[JavaBeanType[_]])
      doMap(source, target, worker, context, path)
      target
    }
  }

  def doMap(source: S, target: T, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder): Unit
}
