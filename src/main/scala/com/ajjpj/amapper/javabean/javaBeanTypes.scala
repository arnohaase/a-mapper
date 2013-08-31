package com.ajjpj.amapper.javabean

import scala.reflect.ClassTag
import com.ajjpj.amapper.core.AType
import java.lang.reflect.ParameterizedType
import scala.annotation.tailrec


trait JavaBeanType[T<:AnyRef] extends AType {
  def cls: Class[T]
  if(cls.isPrimitive) throw new IllegalArgumentException("primitive type passed directly to bean type constructor - use ObjectType factory method instead")

  def isAssignableFrom (other: JavaBeanType[_<:AnyRef]): Boolean
}

trait SingleParamBeanType[T<:AnyRef, P<:AnyRef] extends JavaBeanType[T] {
  def paramCls: Class[P]
  if(paramCls.isPrimitive) throw new IllegalArgumentException("primitive type passed directly to bean type constructor - use ObjectType factory method instead")
  def paramType: JavaBeanType[P] = SimpleJavaBeanType(paramCls)
}

case class SimpleJavaBeanType[T<:AnyRef] (cls: Class[T]) extends JavaBeanType[T] {
  override val name = cls.getName
  override def isAssignableFrom(other: JavaBeanType[_<:AnyRef]) = cls isAssignableFrom other.cls
}

case class SimpleSingleParamBeanType[T<:AnyRef, P<:AnyRef] (cls: Class[T], paramCls: Class[P]) extends SingleParamBeanType[T,P] {
  override val name=cls.getName + "[" + paramCls.getName + "]"
  override def isAssignableFrom(o: JavaBeanType[_<:AnyRef]) = o match {
    case other: SingleParamBeanType[_,_] => cls.isAssignableFrom(other.cls) && paramCls.isAssignableFrom(other.paramCls)
    case _ => false
  }
}

object JavaBeanTypes {
  type Cls = Class[_<:AnyRef]

  def apply[T<:AnyRef] (implicit cls: ClassTag[T]) = SimpleJavaBeanType(cls.runtimeClass.asInstanceOf[Class[T]]).asInstanceOf[JavaBeanType[T]]
  def apply[T<:AnyRef,P<:AnyRef](implicit cls: ClassTag[T], paramCls: ClassTag[P]) =
    SimpleSingleParamBeanType(cls.runtimeClass.asInstanceOf[Class[T]], paramCls.runtimeClass.asInstanceOf[Class[P]]).asInstanceOf[SingleParamBeanType[T,P]]

  def create[T<:AnyRef](cls: Class[T]) = SimpleJavaBeanType(cls)
  def create[T<:AnyRef,EL<:AnyRef](cls: Class[T], paramClass: Class[EL]) = SimpleSingleParamBeanType(cls, paramClass)
  def create(tpe: java.lang.reflect.Type): JavaBeanType[_<:AnyRef] = tpe match {
    case cls: Cls =>                                                     create(cls)
    case pt: ParameterizedType if pt.getActualTypeArguments.size == 1 => create (rawType(pt), rawType(pt.getActualTypeArguments()(0)))
    case t =>                                                            create(rawType(t))
  }

  @tailrec
  def rawType(javaType: java.lang.reflect.Type): Cls = javaType match {
    case cls: Cls => cls
    case pt: ParameterizedType => rawType(pt.getRawType)
  }
  def tpe(javaType: java.lang.reflect.Type): JavaBeanType[_<:AnyRef] = javaType match {
    case cls: Cls => SimpleJavaBeanType(normalized(cls))
    case pt: ParameterizedType if pt.getActualTypeArguments.length == 1 => SimpleSingleParamBeanType (rawType(pt), rawType(pt.getActualTypeArguments()(0)))
    case pt: ParameterizedType => SimpleJavaBeanType(rawType(pt))
  }
  def tpe(javaTypes: Option[java.lang.reflect.Type]*): JavaBeanType[_<:AnyRef] = {
    val candidates = javaTypes.flatMap (x => x).map(create).toSet

    candidates.size match {
      case 1 => candidates.iterator.next()
      case 0 => throw new IllegalArgumentException("no type")
    }
  }

  val primitiveEquivalents = Map(
    classOf[java.lang.Boolean]   -> java.lang.Boolean.TYPE,
    classOf[java.lang.Character] -> java.lang.Character.TYPE,
    classOf[java.lang.Byte]      -> java.lang.Byte.TYPE,
    classOf[java.lang.Short]     -> java.lang.Short.TYPE,
    classOf[java.lang.Integer]   -> java.lang.Integer.TYPE,
    classOf[java.lang.Long]      -> java.lang.Long.TYPE,
    classOf[java.lang.Float]     -> java.lang.Float.TYPE,
    classOf[java.lang.Double]    -> java.lang.Double.TYPE
  )

  def normalized(cls: Class[_]): Class[_<:AnyRef] = if(cls.isPrimitive) cls match {
    case java.lang.Boolean.TYPE   => classOf[java.lang.Boolean]
    case java.lang.Character.TYPE => classOf[java.lang.Character]
    case java.lang.Byte.TYPE      => classOf[java.lang.Byte]
    case java.lang.Short.TYPE     => classOf[java.lang.Short]
    case java.lang.Integer.TYPE   => classOf[java.lang.Integer]
    case java.lang.Long.TYPE      => classOf[java.lang.Long]
    case java.lang.Float.TYPE     => classOf[java.lang.Float]
    case java.lang.Double.TYPE    => classOf[java.lang.Double]
    case _ => cls.asInstanceOf[Cls]
  }
  else cls.asInstanceOf[Cls]
}
