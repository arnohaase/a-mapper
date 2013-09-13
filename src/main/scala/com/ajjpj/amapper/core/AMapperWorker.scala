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
  def identifierExtractor: IdentifierExtractor

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

  def diff(path: PathBuilder, sourceOld: AnyRef, sourceNew: AnyRef, types: QualifiedSourceAndTargetType, oldContext: Map[String, AnyRef], newContext: Map[String, AnyRef], isDerived: Boolean)
  def diffValue(path: PathBuilder, sourceOld: AnyRef, sourceNew: AnyRef, types: QualifiedSourceAndTargetType, oldContext: Map[String, AnyRef], newContext: Map[String, AnyRef], isDerived: Boolean)
  def diffObject(path: PathBuilder, sourceOld: AnyRef, sourceNew: AnyRef, types: QualifiedSourceAndTargetType, oldContextOrig: Map[String, AnyRef], newContextOrig: Map[String, AnyRef], isDerived: Boolean)
  def diffDeferred(path: PathBuilder, sourceOld: AnyRef, sourceNew: AnyRef, types: QualifiedSourceAndTargetType, oldContextOrig: Map[String, AnyRef], newContextOrig: Map[String, AnyRef], isDerived: Boolean)
}


trait IdentifierExtractor {
  /**
   * Used to qualify the path segment in a collection mapping, or more generally to identify an object in the mapper's output.. The mapper itself
   *  does not rely on it being unique, but using code may - especially when creating a 'diff'. <p />
   * The following properties are often desirable for uniqueIdentifier implementations (pretty much the same criteria
   *  that apply to good 'business keys' in persistent storage):
   * <ul>
   * <li> stable:    Running the same code with the same data returns the same key every time (as opposed to e.g. System.identityHashCode)
   * <li> unique:    "Different" elements have different identifiers, i.e. from an application perspective - old and new versions of the "same" element might well have the same identifier
   * <li> selective: Several versions of the "same" element have the same identifier - e.g. correcting a typo in a person's name does not change that person's identifier
   * <li> serializable / human readable: is often a direct consequence of "stable"
   * </ul>
   */
  def uniqueIdentifier(o: AnyRef, tpe: AType): AnyRef
}

