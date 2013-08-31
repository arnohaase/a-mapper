package com.ajjpj.amapper.javabean.propbased

import com.ajjpj.amapper.javabean.JavaBeanType
import java.lang.reflect.{Method, Modifier, Field}
import ognl.Ognl
import com.ajjpj.amapper.javabean.builder.{QualifierExtractor, JavaBeanSupport, IsDeferredStrategy}
import com.ajjpj.amapper.core.{AQualifier, AMapperLogger}
import scala.annotation.tailrec

/**
 * @author arno
 */
trait PropertyAccessor {
  def name: String // for debugging / logging
  def tpe: JavaBeanType[_]

  def sourceQualifier: AQualifier
  def targetQualifier: AQualifier

  def isDeferred: Boolean

  def isReadable: Boolean
  def isWritable: Boolean

  def get(o: AnyRef): AnyRef // primitives are *always* returned 'boxed'
  def set(o: AnyRef, newValue: AnyRef): Unit
}

object PropertyAccessor {
  type Type = JavaBeanType[_<:AnyRef]

  def sharedProperties (sourceClass: Class[_], targetClass: Class[_], isPropDeferred: IsDeferredStrategy, log: AMapperLogger, qualifierExtractor: QualifierExtractor): Traversable[SourceAndTargetProp] =
    new SharedAccessorFactory(sourceClass, targetClass, isPropDeferred, log, qualifierExtractor).sharedAccesors


  def makeAccessible(m: Method) = if (! Modifier.isPublic(m.getModifiers) && !m.isAccessible) m.setAccessible(true)
  private[propbased] def isDeferred(isPropDeferred: IsDeferredStrategy, methods: Option[Method]*) = methods.flatMap(x=>x).exists(isPropDeferred(_))

  private class SharedAccessorFactory (sourceClass: Class[_], targetClass: Class[_], isPropDeferred: IsDeferredStrategy, log: AMapperLogger, qualifierExtractor: QualifierExtractor) {
    type Cls = Class[_<:AnyRef]

    val sourceProps = JavaBeanSupport.allBeanProps(sourceClass, isPropDeferred, qualifierExtractor)
    val targetProps = JavaBeanSupport.allBeanProps(targetClass, isPropDeferred, qualifierExtractor)

    val propNames = sourceProps.keys.filter(x => targetProps.contains(x))

    /**
     * @return *all* property pairs, regardless of their readability / writability
     */
    val sharedAccesors = propNames.map(name => SourceAndTargetProp(sourceProps(name), targetProps(name)))

    //TODO log warning if getter and setter have primitive and wrapped type, respectively


    //TODO element type / parameterized! --> match if they are the same
  }
}

//----------------------------------------------------------------------------------------------------------------------------

case class FieldBasedPropertyAccessor(name: String, field: Field, isDeferred: Boolean, tpe: JavaBeanType[_<:AnyRef], sourceQualifier: AQualifier, targetQualifier: AQualifier) extends PropertyAccessor {
  if(! Modifier.isPublic(field.getModifiers) && ! field.isAccessible)
    field.setAccessible(true)

  val isReadable = true
  val isWritable = ! Modifier.isFinal(field.getModifiers)

  def get(o: AnyRef) = field.get(o)
  def set(o: AnyRef, newValue: AnyRef) = field.set(o, newValue)
}


case class MethodBasedPropertyAccessor(name: String, getter: Option[Method], setter: Option[Method], isDeferred: Boolean, tpe: JavaBeanType[_<:AnyRef], sourceQualifier: AQualifier, targetQualifier: AQualifier) extends PropertyAccessor {
  getter.foreach(PropertyAccessor.makeAccessible)
  setter.foreach(PropertyAccessor.makeAccessible)

  val isReadable = getter.isDefined
  val isWritable = setter.isDefined

  def get(o: AnyRef) = getter.get.invoke(o)
  def set(o: AnyRef, newValue: AnyRef) = setter.get.invoke(o, newValue)
}


case class OgnlPropertyAccessor(name: String, expression: String, parentClass: Class[_], isDeferred: Boolean, tpe: JavaBeanType[_<:AnyRef], sourceQualifier: AQualifier, targetQualifier: AQualifier) extends PropertyAccessor {
  val parsedExpression = Ognl.parseExpression(expression)
  val isReadable = true

  val isWritable = { // use a heuristic to determine whether the OGNL property is writable
    try {
      val o = parentClass.newInstance
      Ognl.setValue(parsedExpression, o, Ognl.getValue(parsedExpression, o))
      true
    }
    catch {
      case _: Exception => false
    }
  }


  def get(o: AnyRef) = Ognl.getValue(parsedExpression, o)
  def set(o: AnyRef, newValue: AnyRef) = Ognl.setValue(parsedExpression, o, newValue)
}


case class MethodPathStep(getter: Method, nullSafe: Boolean) {
  PropertyAccessor.makeAccessible (getter)

  def get(o: AnyRef): AnyRef = {
    if (o==null && nullSafe)
      null
    else
      getter.invoke(o)
  }
}


case class MethodPathBasedPropertyAccessor(name: String, path: List[MethodPathStep], finalGetter: Option[Method], finalSetter: Option[Method], finalStepNullSafe: Boolean, isDeferred: Boolean, tpe: JavaBeanType[_<:AnyRef], sourceQualifier: AQualifier, targetQualifier: AQualifier)
    extends PropertyAccessor {
  val isReadable = finalGetter.isDefined
  val isWritable = finalSetter.isDefined

  finalGetter.foreach(PropertyAccessor.makeAccessible)
  finalSetter.foreach(PropertyAccessor.makeAccessible)

  @tailrec
  private def get(o: Option[AnyRef], remainingPath: List[MethodPathStep]): Option[AnyRef] = remainingPath match {
    case Nil                                              => o
    case head :: tail if o == Some(null) && head.nullSafe => None
    case head :: tail                                     => get(Some(head.get(o.get)), tail)
  }

  def get(o: AnyRef) = get(Some(o), path) match {
    case None                            => null
    case Some(null) if finalStepNullSafe => null
    case Some(beforeLast)                => finalGetter.get.invoke(beforeLast)
  }

  def set(o: AnyRef, newValue: AnyRef) = get(Some(o), path) match {
    case None                            =>
    case Some(null) if finalStepNullSafe =>
    case Some(beforeLast)                => finalSetter.get.invoke(beforeLast, newValue)
  }
}
