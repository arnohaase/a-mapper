package com.ajjpj.amapper.core


/**
 * implementations are required to have an 'equals' method that covers all keys and their values
 */
trait AQualifier {
  def get(key: String): Option[String]
}

case object NoQualifier extends AQualifier {
  override def get(key: String) = None
}

case class MapBasedQualifier (map: Map[String, String]) extends AQualifier {
  override def get(key: String) = map.get(key)
}

/**
 * A value mapping transforms data for which there is no difference between several copies as long as their attribute
 *  values are the same, e.g. strings, numbers or timestamps.
 */
trait AValueMappingDef [S, T, H] extends CanHandleSourceAndTarget {
  /**
   * 'true' means a source value of <code>null</code> is passed to this mapping def, 'false' causes shortcut evaluation to
   *  return <code>null</code> immediately
   */
  def handlesNull: Boolean

  def map(sourceValue: S, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], context: Map[String, AnyRef]): T
}


/**
 * Object mappings deal with data that is sensitive to
 */
trait AObjectMappingDef [S, T, H] extends CanHandleSourceAndTarget {
  /**
   * @return true iff both source and target side have object identity, i.e. calls with the <em>same</em> object
   *         must return the <em>same</em> result. That is almost always desirable - it is one of the key characteristics
   *         of object mappings as opposed to value mappings.<p />
   *         The only context where a value of 'false' makes sense is if a value (e.g. a string) is mapped into an existing
   *         data structure with object identity (e.g. a map).
   */
  def isCacheable: Boolean = true

  def map(source: S, target: T, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: H], context: Map[String, AnyRef], path: PathBuilder): T
}

