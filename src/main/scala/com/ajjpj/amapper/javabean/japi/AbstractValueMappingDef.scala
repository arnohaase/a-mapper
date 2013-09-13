package com.ajjpj.amapper.javabean.japi

import com.ajjpj.amapper.javabean.{JavaBeanType, JavaBeanTypes}
import com.ajjpj.amapper.core.{QualifiedSourceAndTargetType, AValueMappingDef}

/**
 * @author arno
 */
abstract class AbstractValueMappingDef[S<:AnyRef, T<:AnyRef, H<:AnyRef] (sourceClass: Class[S], targetClass: Class[T]) extends AValueMappingDef[S, T, H] {
  val sourceTpe = JavaBeanTypes.create (sourceClass)
  val targetTpe = JavaBeanTypes.create (targetClass)

  override def canHandle(types: QualifiedSourceAndTargetType) = (types.sourceType, types.targetType) match {
    case (st: JavaBeanType[_], tt: JavaBeanType[_]) => sourceTpe.isAssignableFrom(st) && targetTpe.isAssignableFrom(tt)
    case _ => false
  }
  override def handlesNull = true
}
