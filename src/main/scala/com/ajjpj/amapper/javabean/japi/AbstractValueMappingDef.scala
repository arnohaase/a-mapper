package com.ajjpj.amapper.javabean.japi

import com.ajjpj.amapper.javabean.{JavaBeanType, JavaBeanTypes}
import com.ajjpj.amapper.core.{AQualifier, AType, AValueMappingDef}

/**
 * @author arno
 */
abstract class AbstractValueMappingDef[S<:AnyRef, T<:AnyRef, H<:AnyRef] (sourceClass: Class[S], targetClass: Class[T]) extends AValueMappingDef[S, T, H] {
  val sourceTpe = JavaBeanTypes.create (sourceClass)
  val targetTpe = JavaBeanTypes.create (targetClass)

  override def canHandle(sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier) = (sourceType, targetType) match {
    case (st: JavaBeanType[_], tt: JavaBeanType[_]) => sourceTpe.isAssignableFrom(st) && targetTpe.isAssignableFrom(tt)
    case _ => false
  }
  override def handlesNull = true
}
