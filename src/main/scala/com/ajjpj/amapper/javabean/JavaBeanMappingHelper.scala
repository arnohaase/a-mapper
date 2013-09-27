package com.ajjpj.amapper.javabean

import com.ajjpj.amapper.collection._
import com.ajjpj.amapper.core.{IdentifierExtractor, AType}
import java.lang.reflect.Modifier


/**
 * @author arno
 */
trait JavaBeanMappingHelper {
  /**
   * All implicit object creation done by the mapper itself goes through this method. Implementations may use reflection
   *  to call the class' no-args constructor (which is the default behavior), but more sophisticated implementations
   *  could e.g. lookup JPA entities by their primary key, causing the mapper to merge data into persistent data
   *  structures.
   */
  def createInstance[T <: AnyRef](tpe: JavaBeanType[T], forSourceType: JavaBeanType[_]): T
}

object SimpleBeanMappingHelper extends JavaBeanMappingHelper with ACollectionHelper {
  override def createInstance[T <: AnyRef](tpe: JavaBeanType[T], forSourceType: JavaBeanType[_]) = tpe.cls.newInstance

  override def createEmptyMutableCollection[T](tpe: AType, sourceTpe: AType): AMutableCollection[T] = tpe match {
    case SimpleSingleParamBeanType (collClass, _) if ! Modifier.isAbstract(collClass.getModifiers) => ACollectionAdapter(collClass.newInstance.asInstanceOf[java.util.Collection[T]])
    case SimpleSingleParamBeanType (collClass, _) if collClass == classOf[java.util.List[_]]       => ACollectionAdapter(new java.util.ArrayList[T])
    case SimpleSingleParamBeanType (collClass, _) if collClass == classOf[java.util.Set[_]]        => ACollectionAdapter(new java.util.HashSet[T])
    case SimpleSingleParamBeanType (collClass, _) if collClass == classOf[java.util.SortedSet[_]]  => ACollectionAdapter(new java.util.TreeSet[T])
    case _ => throw new IllegalArgumentException (tpe + " is not a (known) collection type")
  }

  override def elementType(tpe: AType): AType = tpe match {
    case SimpleSingleParamBeanType(_, paramCls) => JavaBeanTypes.create(paramCls)
    case _ => throw new IllegalArgumentException("not a parameterized type")
  }

  override def equivalenceMap[S<:AnyRef, T<:AnyRef](sourceColl: Iterable[S], sourceType: AType, targetColl: Iterable[T], targetType: AType, identifierExtractor: IdentifierExtractor): EquivalenceMap[S, T] =
    new ComparisonBasedEquivalenceMap[S,T](sourceColl, targetColl, (s: S, t: T) => identifierExtractor.uniqueIdentifier(s, sourceType) == identifierExtractor.uniqueIdentifier(t, targetType))
}
