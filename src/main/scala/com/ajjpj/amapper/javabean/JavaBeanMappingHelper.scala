package com.ajjpj.amapper.javabean

import com.ajjpj.amapper.collection._
import com.ajjpj.amapper.core.AType
import java.lang.reflect.Modifier


/**
 * @author arno
 */
trait JavaBeanMappingHelper { //TODO modularize?
  /**
   * All implicit object creation done by the mapper itself goes through this method. Implementations may use reflection
   *  to call the type'el no-args constructor (which is the default behavior), but more sophisticated implementations
   *  could e.g. lookup JPA entities by their primary key, causing the mapper to merge data into persistent data
   *  structures.
   */
  def createInstance[T <: AnyRef](tpe: JavaBeanType[T], forSourceType: JavaBeanType[_]): T

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
  def uniqueIdentifier(o: AnyRef, tpe: JavaBeanType[_ <: AnyRef]): AnyRef
}

object SimpleBeanMappingHelper extends JavaBeanMappingHelper with ACollectionHelper {
  def createInstance[T <: AnyRef](tpe: JavaBeanType[T], forSourceType: JavaBeanType[_]) = tpe.cls.newInstance
  def uniqueIdentifier(o: AnyRef, tpe: JavaBeanType[_ <: AnyRef]): AnyRef = o.toString

  def createEmptyMutableCollection[T](tpe: AType, sourceTpe: AType): AMutableCollection[T] = tpe match {
    case SimpleSingleParamBeanType (collClass, _) if ! Modifier.isAbstract(collClass.getModifiers) => ACollectionAdapter(collClass.newInstance.asInstanceOf[java.util.Collection[T]])
    case SimpleSingleParamBeanType (collClass, _) if collClass == classOf[java.util.List[_]]       => ACollectionAdapter(new java.util.ArrayList[T])
    case SimpleSingleParamBeanType (collClass, _) if collClass == classOf[java.util.Set[_]]        => ACollectionAdapter(new java.util.HashSet[T])
    case SimpleSingleParamBeanType (collClass, _) if collClass == classOf[java.util.SortedSet[_]]  => ACollectionAdapter(new java.util.TreeSet[T])
    case _ => throw new IllegalArgumentException (tpe + " is not a (known) collection type")
  }

  def elementType(tpe: AType): AType = tpe match {
    case SimpleSingleParamBeanType(_, paramCls) => JavaBeanTypes.create(paramCls)
    case _ => throw new IllegalArgumentException("not a parameterized type")
  }

  def equivalenceMap[S, T](sourceColl: Iterable[S], sourceType: AType, targetColl: Iterable[T], targetType: AType): EquivalenceMap[S, T] = new ComparisonBasedEquivalenceMap[S,T](sourceColl, targetColl, _==_)
}
