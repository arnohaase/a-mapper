package com.ajjpj.amapper.collection

import com.ajjpj.amapper.core.AType
import scala.collection.mutable.ArrayBuffer
import com.ajjpj.amapper.util.Identity

/**
 * @author arno
 */
trait ACollectionHelper {
  def createEmptyMutableCollection[T](tpe: AType, sourceTpe: AType): AMutableCollection[T]
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