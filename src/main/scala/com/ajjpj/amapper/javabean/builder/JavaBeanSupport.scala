package com.ajjpj.amapper.javabean.builder

import java.lang.reflect.Method
import com.ajjpj.amapper.javabean.{JavaBeanTypes, JavaBeanType}
import com.ajjpj.amapper.javabean.JavaBeanTypes._
import scala.Some
import com.ajjpj.amapper.javabean.propbased.{MethodBasedPropertyAccessor, PropertyAccessor}
import com.ajjpj.amapper.core.{NoQualifier, AQualifier}


/**
 * @author arno
 */
object JavaBeanSupport extends JavaBeanSupport {
  def isDeferred(isPropDeferred: IsDeferredStrategy, methods: Option[Method]*) = methods.flatMap(x=>x).exists(isPropDeferred(_))
}

class JavaBeanSupport {
  //TODO configurable --> public only or all?!
  def allMethods(cls: Class[_]) = cls.getMethods

  def beanProp(cls: Class[_], name: String, isPropDeferred: IsDeferredStrategy, qualifierExtractor: QualifierExtractor) = {
    val setters = asSetterMethods(cls, name).flatMap(x => asSetter(x, isPropDeferred, qualifierExtractor))
    val getters = asGetterMethods(cls, name).flatMap(x => asGetter(x, isPropDeferred, qualifierExtractor))

    (getters.size, setters.size) match {
      case (1, 1) => Some (getters(0).copy(setter = setters(0).setter, setterType = setters(0).setterType, targetQualifier = setters(0).targetQualifier).asProperty)
      case (1, 0) => Some (getters(0).asProperty)
      case (0, 1) => Some (setters(0).asProperty)
      case _      => None
    }
  }

  def allBeanProps(cls: Class[_], isPropDeferred: IsDeferredStrategy, qualifierExtractor: QualifierExtractor): Map[String, PropertyAccessor] = {
    var raw = Map[String, AccessorDetails]() withDefault (name => AccessorDetails (name, None, None, null, null, NoQualifier, NoQualifier, isPropDeferred))

    allMethods(cls).foreach(mtd => {
      asGetter(mtd, isPropDeferred, qualifierExtractor) match {
        case Some(details) =>
          val merged = raw(details.name).copy(getter = details.getter, getterType = details.getterType, sourceQualifier = details.sourceQualifier)
          raw += (details.name -> merged)
        case None =>
      }
      asSetter(mtd, isPropDeferred, qualifierExtractor) match {
        case Some(details) =>
          //TODO check for duplicates and warn, keep only the one matching the getter
          val merged = raw(details.name).copy(setter = details.setter, setterType = details.setterType, targetQualifier = details.targetQualifier)
          raw += (details.name -> merged)
        case None =>
      }
    })

    raw.values.map(x => x.name -> x.asProperty).toMap
  }

  def asUniqueGetterMethod(cls: Class[_], propName: String): Option[Method] = asGetterMethods(cls, propName) match {
    case s if s.length == 1 => Some(s(0))
    case _ => None
  }

  def asGetterMethods(cls: Class[_], propName: String): Seq[Method] = {
    val getterNames = this.getterNames(propName)
    allMethods(cls).filter(x => getterNames.contains(x.getName)).flatMap(x => asGetter(x, null, NoQualifierExtractor)).flatMap(_.getter)
  }

  def asUniqueSetterMethod(cls: Class[_], propName: String): Option[Method] = asSetterMethods(cls, propName) match {
    case s if s.length == 1 => Some(s(0))
    case _ => None
  }

  def asSetterMethods(cls: Class[_], propName: String): Seq[Method] = {
    val setterName = this.setterName(propName)
    allMethods(cls).filter(_.getName == setterName).flatMap(x => asSetter(x, null, NoQualifierExtractor)).flatMap(_.setter)
  }

  def asGetter(mtd: Method, isPropDeferred: IsDeferredStrategy, qualifierExtractor: QualifierExtractor): Option[AccessorDetails] = mtd match {
    case _ if mtd.getDeclaringClass == classOf[java.lang.Object] => None
    case _ if mtd.getParameterTypes.size != 0 => None
    case _ if mtd.getName startsWith "get" => Some (AccessorDetails(beanPropName(mtd.getName.substring(3)), Some(mtd), None, tpe(mtd.getGenericReturnType), null, qualifierExtractor.extract(mtd), NoQualifier, isPropDeferred))
    case _ if JavaBeanTypes.normalized(mtd.getReturnType) != classOf[java.lang.Boolean] => None
    case _ if mtd.getName startsWith "is"  => Some (AccessorDetails(beanPropName(mtd.getName.substring(2)), Some(mtd), None, tpe(mtd.getGenericReturnType), null, qualifierExtractor.extract(mtd), NoQualifier, isPropDeferred))
    case _ if mtd.getName startsWith "has" => Some (AccessorDetails(beanPropName(mtd.getName.substring(3)), Some(mtd), None, tpe(mtd.getGenericReturnType), null, qualifierExtractor.extract(mtd), NoQualifier, isPropDeferred))
    case _ => None
  }

  def asSetter(mtd: Method, isPropDeferred: IsDeferredStrategy, qualifierExtractor: QualifierExtractor): Option[AccessorDetails] = mtd match {
    case _ if mtd.getName.startsWith("set") && mtd.getParameterTypes.size == 1 =>
      Some(AccessorDetails(beanPropName(mtd.getName.substring(3)), None, Some(mtd), null, tpe(mtd.getGenericParameterTypes()(0)), NoQualifier, qualifierExtractor.extract(mtd), isPropDeferred))
    case _ => None
  }

  private def toFirstUpper(s: String) = s(0).toUpper + s.substring(1)

  def setterName(propName: String) = "set" + toFirstUpper(propName)
  def getterNames(propName: String) = Set("get" + toFirstUpper(propName), "is" + toFirstUpper(propName), "has" + toFirstUpper(propName))

  def beanPropName(rawNameWithoutPrefix: String): String = rawNameWithoutPrefix match {
    case n if n.length > 1 && n.charAt(1).isUpper => n // special case defined in Java Beans standard
    case n => n(0).toLower + n.substring(1)
  }
}

case class AccessorDetails(name: String, getter: Option[Method], setter: Option[Method], getterType: JavaBeanType[_<:AnyRef], setterType: JavaBeanType[_<:AnyRef], sourceQualifier: AQualifier, targetQualifier: AQualifier, isPropDeferred: IsDeferredStrategy) {
  def isDeferred = JavaBeanSupport.isDeferred(isPropDeferred, getter, setter)
  def asProperty = MethodBasedPropertyAccessor(name, getter, setter, isDeferred, if(getter.isDefined) getterType else setterType, sourceQualifier, targetQualifier) //TODO check that they match
}
