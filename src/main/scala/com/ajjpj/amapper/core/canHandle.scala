package com.ajjpj.amapper.core

/**
 * @author arno
 */
case class QualifiedType (tpe: AType, qualifier: AQualifier)
case class QualifiedSourceAndTargetType (sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier) {
  def source = QualifiedType(sourceType, sourceQualifier)
  def target = QualifiedType(targetType, targetQualifier)
}

trait CanHandleTypeAndQualifier {
  def canHandle(qt: QualifiedType): Boolean
}

trait CanHandleSourceAndTarget {
  def canHandle(types: QualifiedSourceAndTargetType): Boolean
}

class CanHandleTypeAndQualifierCache[T <: CanHandleTypeAndQualifier] (all: Seq[T]) {
  @volatile private var resolved: Map[QualifiedType, Option[T]] = Map()

  def entryFor(key: QualifiedType): Option[T] = {
    resolved.get(key) match {
      case Some(v) => v
      case None =>
        // In a multi threaded situation the same combination of types may be resolved multiple
        //  times concurrently, with later results overwriting earlier. This does not cause harm
        //  and allows faster access to existing cached values because there is no locking.
        val result = all.find(_.canHandle(key))
        resolved += (key -> result)
        result
    }
  }
}

class CanHandleSourceAndTargetCache[T <: CanHandleSourceAndTarget] (all: Seq[T]) {
  @volatile private var resolved: Map[QualifiedSourceAndTargetType, Option[T]] = Map()

  def entryFor(key: QualifiedSourceAndTargetType): Option[T] = {
    resolved.get(key) match {
      case Some(v) => v
      case None =>
        // In a multi threaded situation the same combination of types may be resolved multiple
        //  times concurrently, with later results overwriting earlier. This does not cause harm
        //  and allows faster access to existing cached values because there is no locking.
        val result = all.find(_.canHandle(key))
        resolved += (key -> result)
        result
    }
  }
}
