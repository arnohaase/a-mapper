package com.ajjpj.amapper.core.impl

import java.util.IdentityHashMap
import scala.collection.convert.Wrappers
import com.ajjpj.amapper.core.{AMapperException, PathBuilder}

/**
 * @author arno
 */
class IdentityCache {
  private val map = Wrappers.JMapWrapper (new IdentityHashMap[AnyRef, AnyRef]())

  def register(source: AnyRef, target: AnyRef, path: PathBuilder) {
    map.get(source) match {
      case Some(prev) =>
        if(prev eq target)
          throw new AMapperException(s"duplicate registration of same target $target for $source", path.build)
        else
          throw new  AMapperException(s"duplicate registration of different target $target for $source (was: $prev)", path.build)
      case None => map += (source -> target)
    }
  }

  def get(source: AnyRef): Option[AnyRef] = map.get(source)
}
