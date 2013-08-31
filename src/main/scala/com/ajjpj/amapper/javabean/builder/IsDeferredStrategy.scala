package com.ajjpj.amapper.javabean.builder

import java.lang.reflect.Method
import com.ajjpj.amapper.javabean.japi.DeferredProperty

/**
 * @author arno
 */
trait IsDeferredStrategy {
  def apply(mtd: Method): Boolean
}

case class LiteralIsDeferred(isDeferred: Boolean) extends IsDeferredStrategy {
  override def apply(mtd: Method) = isDeferred
}

class DefaultIsDeferredStrategy extends IsDeferredStrategy {
  def apply(mtd: Method) = mtd.getAnnotation(classOf[DeferredProperty]) != null
}

object DefaultIsDeferredStrategy extends DefaultIsDeferredStrategy
