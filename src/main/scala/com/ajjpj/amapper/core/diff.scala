package com.ajjpj.amapper.core

/**
 * @author arno
 */
class Diff(elements: List[DiffElement])


sealed trait ObjectRef
case class ByPathRef(path: Path) extends ObjectRef
case class ByIdentifierRef(identifier: AnyRef) extends ObjectRef
case class NewObjectRef() extends ObjectRef


sealed trait DiffElement {
  def path: Path
  /**
   * marks changes that were caused by structural changes further up in the graph
   */
  def isDerived: Boolean
}
case class AttributeDiffElement[T] (path: Path, oldValue: T, newValue: T, isDerived: Boolean) extends DiffElement
case class ChangeRefDiffElement    (path: Path, newValue: ObjectRef,      isDerived: Boolean) extends DiffElement
case class AddDiffElement          (path: Path, newValue: ObjectRef,      isDerived: Boolean) extends DiffElement
case class RemoveDiffElement       (path: Path, oldValue: ObjectRef,      isDerived: Boolean) extends DiffElement



