package com.ajjpj.amapper.core

/**
 * @author arno
 */
class Diff(elements: List[DiffElement])


sealed trait DiffElement {
  def path: Path
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
case class AttributeDiffElement (path: Path, oldValue: AnyRef, newValue: AnyRef, isDerived: Boolean) extends DiffElement

/**
 * for changes of objects, with 'oldValue' and 'newValue' containing the 'unique identifiers' as returned by an IdentifierExtractor
 */
case class ChangeRefDiffElement (path: Path, oldValue: AnyRef, newValue: AnyRef, isDerived: Boolean) extends DiffElement
case class AddDiffElement       (path: Path,                   newValue: AnyRef, isDerived: Boolean) extends DiffElement {
  val oldValue = null
}
case class RemoveDiffElement    (path: Path, oldValue: AnyRef,                   isDerived: Boolean) extends DiffElement {
  val newValue = null
}



