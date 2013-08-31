package com.ajjpj.amapper.collection

import scala.collection.generic.{Shrinkable, Growable}
import scala.collection.convert.Wrappers

/**
 * @author arno
 */
object ACollectionAdapter {
  def apply[T] (inner: Iterable[T] with Growable[T] with Shrinkable[T]) = new ScalaCollectionAdapter(inner)
  def apply[T] (inner: java.util.Collection[T]) = new JavaCollectionAdapter(inner)
}

class ScalaCollectionAdapter[T] (val underlying: Iterable[T] with Growable[T] with Shrinkable[T]) extends AMutableCollection[T] {
  override def asIterable = underlying
  override def add(o: T) = underlying += o
  override def remove(o: T) = underlying -= o
}

class JavaCollectionAdapter[T] (val underlying: java.util.Collection[T]) extends AMutableCollection[T] {
  override def asIterable = Wrappers.JCollectionWrapper(underlying)
  override def add(o: T) = underlying.add(o)
  override def remove(o: T) = underlying.remove(o)
}
