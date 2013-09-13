package com.ajjpj.amapper.javabean

import com.ajjpj.amapper.core.{AType, AContextExtractor}
import com.ajjpj.amapper.javabean.japi.AContextMarker

/**
 * registers an object as context value for all interfaces it implements that are annotated @AContextMarker
 *
 * @author arno
 */
class AnnotationBasedContextExtractor extends AContextExtractor {
  @volatile var cache = Map[AType, List[String]]()

  def withContext(context: Map[String, AnyRef], o: AnyRef, tpe: AType): Map[String, AnyRef] = {
    cache.get(tpe) match {
      case Some(Nil) =>
        context
      case Some(keys) =>
        context ++ keys.map(_ -> o).toMap
      case None =>
        val keys = extractKeys(tpe)
        cache += (tpe -> keys)
        context ++ keys.map(_ -> o).toMap
    }
  }

  def extractKeys(tpe: AType) = tpe match {
    //TODO recursive interface analysis; move this to JavaBeanSupport
    case SimpleJavaBeanType(cls) => cls.getInterfaces.filter(_.getAnnotation(classOf[AContextMarker]) != null).map(_.getName).toList
    case _ => Nil
  }
}

object AnnotationBasedContextExtractor extends AnnotationBasedContextExtractor
