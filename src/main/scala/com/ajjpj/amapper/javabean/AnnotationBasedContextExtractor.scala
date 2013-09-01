package com.ajjpj.amapper.javabean

import com.ajjpj.amapper.core.{AType, AContextExtractor}
import com.ajjpj.amapper.javabean.japi.AContextMarker

/**
 * registers an object as context value for all interfaces it implements that are annotated @AContextMarker
 *
 * @author arno
 */
class AnnotationBasedContextExtractor extends AContextExtractor {
  def withContext(context: Map[String, AnyRef], o: AnyRef, tpe: AType): Map[String, AnyRef] = {
    tpe match {
      case SimpleJavaBeanType(cls) => fromClass(context, o, cls)
      case _ => context
    }
  }

  def fromClass(context: Map[String, AnyRef], o: AnyRef, cls: Class[_]) = {
    //TODO caching
    //TODO recursive interface analysis; move this to JavBeanSupport
    context ++ cls.getInterfaces.filter(_.getAnnotation(classOf[AContextMarker]) != null).map(iface => (iface.getName -> o)).toMap
  }
}

object AnnotationBasedContextExtractor extends AnnotationBasedContextExtractor
