package com.ajjpj.amapper.core


/**
 * @author arno
 */
trait APreProcessor extends CanHandleSourceAndTarget {
  /**
   * transforms a source object before it is actually mapped. Returning Some(x) causes 'x' to be used for the
   *  actual processing (which may or may not be the same object as 'o'), while returning None causes the
   *  object to be ignored for actual processing.
   */
  def preProcess[T <: AnyRef](o: T, qt: QualifiedSourceAndTargetType): Option[T]
}


trait APostProcessor extends CanHandleSourceAndTarget {
  def postProcess[T <: AnyRef] (o: T, qt: QualifiedSourceAndTargetType): T
}
