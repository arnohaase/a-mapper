package com.ajjpj.amapper.core.impl

import com.ajjpj.amapper.core.{AQualifier, CanHandleTypes, AType}

/**
 * @author arno
 */
class MappingDefResolver[T <: CanHandleTypes] (all: Seq[T]) {
  @volatile private var resolved: Map[(AType, AType, AQualifier, AQualifier), Option[T]] = Map()

  def mappingDefFor(sourceType: AType, targetType: AType, sourceQualifier: AQualifier, targetQualifier: AQualifier): Option[T] = {
    val key = (sourceType, targetType, sourceQualifier, targetQualifier)
    resolved.get(key) match {
      case Some(v) => v
      case None =>
        // In a multi threaded situation the same combination of types may be resolved multiple
        //  times concurrently, with later results overwriting earlier. This does not cause harm
        //  and allows faster access to existing cached values because there is no locking.
        val result = all.find(_.canHandle(sourceType, sourceQualifier, targetType, targetQualifier))
        resolved += (key -> result)
        result
    }
  }
}


