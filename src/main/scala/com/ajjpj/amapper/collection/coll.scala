package com.ajjpj.amapper.collection


/**
 * @author arno
 */
trait AMutableCollection[T] {
  /** for reading the elements */
  def asIterable: Iterable[T]

  /**
   * An instance of AMutableCollection is usually a wrapper around a Scala / Java / XML / ... collection, and this method
   *  allows access to the 'real' collection
   */
  def underlying: AnyRef

  /**
   * for modifying the collection. Implementations must be 'write through', i.e. affect the underlying collection.
   */
  def add(o: T)
  def remove(o: T)
}

trait AMutableSequence[T] extends AMutableCollection[T] {
   //TODO
}
