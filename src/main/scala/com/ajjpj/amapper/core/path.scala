package com.ajjpj.amapper.core

import scala.collection.convert.Wrappers

/**
 * @author arno
 */
case class APath(segments: List[PathSegment]) {
  def this(segments: PathSegment*) = this(segments.toList)
  def this(segments: java.util.List[_ <: PathSegment]) = this(Wrappers.JListWrapper(segments).toList)

  def parent = APath(segments.reverse.tail.reverse)
  def last = segments.reverse.head

  override def toString = "Path{" + segments.mkString(".") + "}"
}

class PathBuilder(reversePath: List[PathSegment]) {
  def this() = this (Nil)
  def +(segment: PathSegment) = new PathBuilder(segment :: reversePath)
  def build = APath (reversePath.reverse)
}

sealed trait PathSegment {
  def name: String
}
case class SimplePathSegment(name: String) extends PathSegment {
  override def toString = name
}
/**
 * combination of name and key must be unique per location in an object graph. For a Set,
 *  the key should be a tuple of attributes that the equals method is based on.
 */
//TODO is that necessary? not implemented that way as yet
case class ParameterizedPathSegment(name: String, key: AnyRef) extends PathSegment {
  override def toString = name + "(" + key + ")"
}
case class IndexedPathSegment(name: String, index: Int) extends PathSegment {
  override def toString = name + "[" + index + "]"
}

