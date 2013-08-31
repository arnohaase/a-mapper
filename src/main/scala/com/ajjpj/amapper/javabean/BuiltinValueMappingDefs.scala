package com.ajjpj.amapper.javabean

import com.ajjpj.amapper.core.{AQualifier, AMapperWorker, AType, AValueMappingDef}
import scala.reflect.ClassTag
import java.util.Date


/**
 * @author arno
 */
object BuiltinValueMappingDefs {
  object StringMappingDef     extends PassThroughValueType[String]
  object BooleanMappingDef    extends PassThroughValueType[java.lang.Boolean]
  object DateMappingDef       extends PassThroughValueType[Date]
  object ClassMappingDef      extends PassThroughValueType[Class[_]]
  object CharMappingDef       extends PassThroughValueType[java.lang.Character]
  object BigDecimalMappingDef extends PassThroughValueType[BigDecimal]

  object EnumMappingDef extends PassThroughValueType[Enum[_<:AnyRef]]

  object ByteMappingDef   extends FromNumberValueType((n: Number) => java.lang.Byte.valueOf    (n.byteValue))
  object ShortMappingDef  extends FromNumberValueType((n: Number) => java.lang.Short.valueOf   (n.shortValue))
  object IntMappingDef    extends FromNumberValueType((n: Number) => java.lang.Integer.valueOf (n.intValue))
  object LongMappingDef   extends FromNumberValueType((n: Number) => java.lang.Long.valueOf    (n.longValue))
  object FloatMappingDef  extends FromNumberValueType((n: Number) => java.lang.Float.valueOf   (n.floatValue))
  object DoubleMappingDef extends FromNumberValueType((n: Number) => java.lang.Double.valueOf  (n.doubleValue))

  object CharToStringMappingDef extends AbstractValueMappingDef[java.lang.Character, String, AnyRef] {
    override def map(sourceValue: Character, sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier, worker: AMapperWorker[_ <: AnyRef]) = String.valueOf(sourceValue)
  }
  object StringToCharMappingDef extends AbstractValueMappingDef[String, java.lang.Character, AnyRef] {
    override def map(sourceValue: String, sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier, worker: AMapperWorker[_ <: AnyRef]) = sourceValue.charAt(0)
  }

  //--------------------------------------

  abstract class AbstractValueMappingDef[S<:AnyRef, T<:AnyRef, H<:AnyRef](implicit srcTag: ClassTag[S], targetTag: ClassTag[T]) extends AValueMappingDef[S, T, H] {
    val sourceTpe = JavaBeanTypes[S]
    val targetTpe = JavaBeanTypes[T]

    override def canHandle(sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier) = (sourceType, targetType) match {
      case (st: JavaBeanType[_], tt: JavaBeanType[_]) => sourceTpe.isAssignableFrom(st) && targetTpe.isAssignableFrom(tt)
      case _ => false
    }
    override def handlesNull = true
  }

  abstract class PassThroughValueType[T<:AnyRef](implicit clsTag: ClassTag[T]) extends AbstractValueMappingDef[T, T, AnyRef] {
    override def canHandle(sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier) = super.canHandle(sourceType, sourceQualifier, targetType, targetQualifier) && sourceType == targetType
    override def map(sourceValue: T, sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier, worker: AMapperWorker[_ <: AnyRef]) = sourceValue
  }

  abstract class FromNumberValueType[T<:AnyRef](extractor: Number => T)(implicit clsTag: ClassTag[T]) extends AbstractValueMappingDef[Number, T, AnyRef] {
    override def map(sourceValue: Number, sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier, worker: AMapperWorker[_ <: AnyRef]) = extractor(sourceValue)
  }
}


