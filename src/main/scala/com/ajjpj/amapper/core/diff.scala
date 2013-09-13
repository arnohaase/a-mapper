package com.ajjpj.amapper.core

/**
 * @author arno
 */
class ADiff(val elements: Iterable[ADiffElement]) {
  val paths: Set[APath] = elements.map(_.path).toSet
  val byPath: Map[APath, Iterable[ADiffElement]] = paths.map(p => p -> elements.filter(_.path == p)).toMap
  val byPathString: Map[String, Iterable[ADiffElement]] = byPath.map(e => e._1.segments.map(_.name).mkString(".") -> e._2)
  def getSingle(path: APath): Option[ADiffElement] = byPath.get(path).map(_.find(_ => true)).getOrElse(None)
  def getSingle(path: String): Option[ADiffElement] = byPathString.get(path).map(_.find(_ => true)).getOrElse(None)
}

class ADiffBuilder {
  private var elements = Vector[ADiffElement]()
  def add(diffElement: ADiffElement) = elements :+= diffElement
  def build = new ADiff(elements)
}


sealed trait ADiffElement {
  def path: APath
  /**
   * marks changes that were caused by structural changes further up in the graph
   */
  def isDerived: Boolean

  /**
   * from the <em>target</em> perspective
   */
  def oldValue: AnyRef
  /**
   * from the <em>target</em> perspective
   */
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



