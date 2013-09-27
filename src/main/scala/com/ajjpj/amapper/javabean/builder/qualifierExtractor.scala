package com.ajjpj.amapper.javabean.builder

import com.ajjpj.amapper.core.{NoQualifier, MapBasedQualifier, AQualifier}
import java.lang.reflect.{Field, Method}
import java.lang.annotation.Annotation
import com.ajjpj.amapper.javabean.japi.AQualifierAnnotation


/**
 * @author arno
 */
trait QualifierExtractor {
  def extract(member: Method): AQualifier
  def extract(member: Field):  AQualifier
}

class DefaultQualifierExtractor extends QualifierExtractor {
  override def extract(member: Method) = extract(member.getAnnotations)
  override def extract(member: Field) = extract(member.getAnnotations)

  private def extract(annotations: Iterable[Annotation]) = {
    //TODO caching of whether annotations designate qualifiers or not

    def key(a: Annotation) = a.annotationType().getAnnotation(classOf[AQualifierAnnotation]).name()
    def value(a: Annotation) = try {
      a.annotationType().getMethod("value").invoke(a).toString
    }
    catch {
      case _: NoSuchMethodException => ""
    }

    val map = annotations
      .filter(_.annotationType().getAnnotation(classOf[AQualifierAnnotation]) != null)
      .map(a => key(a) -> value(a))
      .toMap

    MapBasedQualifier(map)
  }
}

object DefaultQualifierExtractor extends DefaultQualifierExtractor

class NoQualifierExtractor extends QualifierExtractor {
  def extract(member: Method): AQualifier = NoQualifier
  def extract(member: Field): AQualifier = NoQualifier
}
object NoQualifierExtractor extends NoQualifierExtractor