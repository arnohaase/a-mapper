package com.ajjpj.amapper.core


/**
 * A 'worker' is created per mapping execution. It has mutable state and is <em>not</em> thread safe.
 *
 * @author arno
 */
trait AMapperWorker[H] {
  def logger: AMapperLogger

  /**
   * The mapper worker passes the 'helpers' object through to the mapping defs. 'Helpers' can have mutable state, so
   *  their lifecycle is tied to the worker' lifecycle rather than having to be global.
   */
  def helpers: H

  def map(path: PathBuilder, source: AnyRef, target: AnyRef, types: QualifiedSourceAndTargetType, context: Map[String, AnyRef]): Option[AnyRef]

  def mapValue(path: PathBuilder, source: AnyRef, types: QualifiedSourceAndTargetType, context: Map[String, AnyRef]): AnyRef
  def mapObject(path: PathBuilder, source: AnyRef, target: AnyRef, types: QualifiedSourceAndTargetType, context: Map[String, AnyRef]): Option[AnyRef]

  /**
   * deferred mapping causes this mapping to be deferred until no non-deferred mapping work is left. The underlying
   *  abstraction is that there is a primary, hierarchical structure through which all objects can be
   *  reached, and a secondary structure of non-containment references. <p />
   * The former is mapped first so as to provide well-defined, 'normalized' paths to every element. <p />
   * All objects must be reachable through the primary hierarchy. Put differently, it is invalid for
   *  an unmapped object to be reached through a deferred 'map' call. <p />
   * NB: deferred mapping automatically means mutable state in the target object structure because the result of the
   *  mapping is created after the initial object structure is complete. This also prevents streaming processing.
   *
   */
  def mapDeferred(path: PathBuilder, source: AnyRef, target: => AnyRef, types: QualifiedSourceAndTargetType, callback: AnyRef => Unit): Unit
}


