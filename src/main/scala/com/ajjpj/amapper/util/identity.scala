package com.ajjpj.amapper.util

import scala.collection.convert.Wrappers


/**
 * This is a convenience factory for Scala sets based on identity rather than 'equals'
 *
 * @author arno
 */
object IdentitySet {
  def apply[T](): scala.collection.mutable.Set[T] = Wrappers.JSetWrapper (java.util.Collections.newSetFromMap(new java.util.IdentityHashMap[T, java.lang.Boolean]))
  def apply[T](els: T*): scala.collection.mutable.Set[T] = apply[T]().++ (els)
}

class Identity[T] (val e: T) {
  val isObject = e.isInstanceOf[AnyRef]
  override def equals(o: Any): Boolean = o match {
    case i: Identity[_] if  isObject &&  i.isObject => e.asInstanceOf[AnyRef] eq i.e.asInstanceOf[AnyRef]
    case i: Identity[_] if !isObject && !i.isObject => e == i.e
    case _ => false
  }

  override def hashCode() = System.identityHashCode(e)
}

object Identity {
  def apply[T] (o: T) = new Identity(o)
}
