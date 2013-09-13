package com.ajjpj.amapper.core

/**
 * @author arno
 */
class ADiff(elements: Iterable[ADiffElement])


sealed trait ADiffElement {
  def path: APath
  /**
   * marks changes that were caused by structural changes further up in the graph
   */
  def isDerived: Boolean

  def oldValue: AnyRef
  def newValue: AnyRef
}

/**
 * for changes to values, with 'oldValue' and 'newValue' denoting the actual values
 */
case class AttributeDiffElement (path: APath, oldValue: AnyRef, newValue: AnyRef, isDerived: Boolean) extends ADiffElement

/**
 * for changes of objects, with 'oldValue' and 'newValue' containing the 'unique identifiers' as returned by an IdentifierExtractor
 */
case class ChangeRefDiffElement (path: APath, oldValue: AnyRef, newValue: AnyRef, isDerived: Boolean) extends ADiffElement
case class AddDiffElement       (path: APath,                   newValue: AnyRef, isDerived: Boolean) extends ADiffElement {
  val oldValue = null
}
case class RemoveDiffElement    (path: APath, oldValue: AnyRef,                   isDerived: Boolean) extends ADiffElement {
  val newValue = null
}



