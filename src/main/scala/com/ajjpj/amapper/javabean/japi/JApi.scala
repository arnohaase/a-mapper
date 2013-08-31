package com.ajjpj.amapper.javabean.japi

import com.ajjpj.amapper.javabean.{JavaBeanTypes, SimpleBeanMappingHelper, JavaBeanMappingHelper}
import java.util.concurrent.Callable
import com.ajjpj.amapper.core.{MapBasedQualifier, NoQualifier}
import com.ajjpj.amapper.javabean.builder.DefaultQualifierExtractor


/**
 * @author arno
 */
object JApi {
  def typeOf[T](cls: Class[T]) = JavaBeanTypes.create(cls)
  def asFunction[T] (code: Callable[T]) = () => code.call()
  def asFunction (deProxyStrategy: DeProxyStrategy) = (o: AnyRef) => deProxyStrategy.deproxy(o)

  val simpleBeanMappingHelper: Callable[JavaBeanMappingHelper] = new Callable[JavaBeanMappingHelper] {
    override def call() = SimpleBeanMappingHelper
  }

  val defaultQualifierExtractor = DefaultQualifierExtractor

  val noQualifier = NoQualifier
  def qualifier(keyValue: String*) = {
    def asMap(keyValue: List[String]): Map[String, String] = keyValue match {
      case head :: tail => asMap(tail.tail) + (head -> tail.head)
      case Nil => Map()
    }

    if(keyValue.size == 0)
      NoQualifier
    else
      MapBasedQualifier(asMap(keyValue.toList))
  }
}
