package com.ajjpj.amapper.collection

import com.ajjpj.amapper.core.AType
import scala.collection.mutable.ArrayBuffer
import com.ajjpj.amapper.util.Identity

/**
 * @author arno
 */
trait ACollectionHelper {
  def createEmptyMutableCollection[T](tpe: AType, sourceTpe: AType): AMutableCollection[T]

  /**
   * Used to qualify the path segment in a collection mapping. The mapper itself does not rely on it being unique, but using
   *  code may - especially when creating a 'diff'. <p />
   * The following properties are often desirable for uniqueIdentifier implementations (pretty much the same criteria
   *  that apply to good 'business keys' in persistent storage):
   * <ul>
   * <li> stable:    Running the same code with the same data returns the same key every time (as opposed to e.g. System.identityHashCode)
   * <li> unique:    "Different" elements have different identifiers, i.e. from an application perspective - old and new versions of the "same" element might well have the same identifier
   * <li> selective: Several versions of the "same" element have the same identifier - e.g. correcting a typo in a person'el name does not change that person'el identifier
   * <li> serializable / human readable: is often a direct consequence of "stable"
   * </ul>
   */
  def uniqueIdentifier(o: AnyRef, tpe: AType): AnyRef = o.toString

  def elementType(tpe: AType): AType

  def equivalenceMap[S,T](sourceColl: Iterable[S], sourceType: AType, targetColl: Iterable[T], targetType: AType): EquivalenceMap[S, T]
}


trait EquivalenceMap[S, T] {
  def source: Iterable[S]
  def target: Iterable[T]

  def sourcesWithTargetEquivalent: Iterable[S]
  def targetEquivalents(s: S): Iterable[TargetEquivalent[T]]

  def sourceWithoutTarget: Set[S]
  def targetWithoutSource: List[T]
}

case class TargetEquivalent[T] (el: T, count: Int)

class ComparisonBasedEquivalenceMap[S, T](val source: Iterable[S], val target: Iterable[T], equiv: (S, T) => Boolean) extends EquivalenceMap[S,T] {
  private var _equivMap = Map[S, Iterable[TargetEquivalent[T]]]()
  private var _sourceWithoutTarget = ArrayBuffer[S]()
  private var _targetsWithoutSource = ArrayBuffer[T]()
  _targetsWithoutSource ++= target

  source.foreach(s => target.filter(t => equiv(s,t)) match {
    case ts if ts.isEmpty => _sourceWithoutTarget += s
    case ts =>
      _equivMap += (s -> ts.groupBy(x => Identity(x)).map(x => TargetEquivalent(x._1.e, x._2.size)))
      _targetsWithoutSource --= ts
  })

  val equivMap = _equivMap.mapValues (_.toSet)

  def sourcesWithTargetEquivalent = equivMap.keys
  def targetEquivalents(s: S) = equivMap(s)

  val sourceWithoutTarget = _sourceWithoutTarget.toSet
  val targetWithoutSource = _targetsWithoutSource.toList
}