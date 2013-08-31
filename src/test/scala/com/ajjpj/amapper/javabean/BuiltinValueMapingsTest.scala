package com.ajjpj.amapper.javabean

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import java.util.Date
import java.lang.annotation.{ElementType, RetentionPolicy}
import com.ajjpj.amapper.core.NoQualifier
import scala.reflect.ClassTag

/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class BuiltinValueMapingsTest extends FunSuite with ShouldMatchers {
  def typeOf[cls](implicit clsTag: ClassTag[cls]) = SimpleJavaBeanType(clsTag.runtimeClass.asInstanceOf[Class[_<:AnyRef]])
  
  test("string") {
    BuiltinValueMappingDefs.StringMappingDef.canHandle(typeOf[String], NoQualifier, typeOf[String], NoQualifier) should equal (true)
    BuiltinValueMappingDefs.StringMappingDef.canHandle(typeOf[String], NoQualifier, typeOf[AnyRef], NoQualifier) should equal (false)
    BuiltinValueMappingDefs.StringMappingDef.canHandle(typeOf[AnyRef], NoQualifier, typeOf[String], NoQualifier) should equal (false)
    BuiltinValueMappingDefs.StringMappingDef.canHandle(typeOf[String], NoQualifier, typeOf[Date],   NoQualifier) should equal (false)
    BuiltinValueMappingDefs.StringMappingDef.canHandle(typeOf[Date],   NoQualifier, typeOf[String], NoQualifier) should equal (false)

    BuiltinValueMappingDefs.StringMappingDef.map("a",   null, null, null, null, null) should equal("a")
    BuiltinValueMappingDefs.StringMappingDef.map("bcd", null, null, null, null, null) should equal("bcd")
    BuiltinValueMappingDefs.StringMappingDef.map(null,  null, null, null, null, null) should equal(null)
  }

  test("boolean") {
    BuiltinValueMappingDefs.BooleanMappingDef.canHandle (typeOf[java.lang.Boolean], NoQualifier, typeOf[java.lang.Boolean], NoQualifier) should equal (true)
//TODO test this at the 'builder' level BuiltinValueMappingDefs.BooleanMappingDef.canHandle (SimpleJavaBeanType (java.lang.Boolean.TYPE),     SimpleJavaBeanType(java.lang.Boolean.TYPE))     should equal (true)

    BuiltinValueMappingDefs.BooleanMappingDef.map(true,                         null, null, null, null, null) should equal(true)
    BuiltinValueMappingDefs.BooleanMappingDef.map(new java.lang.Boolean(true),  null, null, null, null, null) should equal(true)
    BuiltinValueMappingDefs.BooleanMappingDef.map(false,                        null, null, null, null, null) should equal(false)
    BuiltinValueMappingDefs.BooleanMappingDef.map(new java.lang.Boolean(false), null, null, null, null, null) should equal(false)
    BuiltinValueMappingDefs.BooleanMappingDef.map(null,                         null, null, null, null, null) should equal(null)
  }

  test("enum") {
    BuiltinValueMappingDefs.EnumMappingDef.canHandle (typeOf[RetentionPolicy], NoQualifier, typeOf[RetentionPolicy], NoQualifier) should equal (true)
    BuiltinValueMappingDefs.EnumMappingDef.canHandle (typeOf[ElementType],     NoQualifier, typeOf[ElementType],     NoQualifier)     should equal (true)
    BuiltinValueMappingDefs.EnumMappingDef.canHandle (typeOf[RetentionPolicy], NoQualifier, typeOf[ElementType],     NoQualifier)     should equal (false)
    BuiltinValueMappingDefs.EnumMappingDef.canHandle (typeOf[ElementType],     NoQualifier, typeOf[RetentionPolicy], NoQualifier) should equal (false)

    BuiltinValueMappingDefs.EnumMappingDef.map(RetentionPolicy.RUNTIME, null, null, null, null, null).asInstanceOf[AnyRef] should equal (RetentionPolicy.RUNTIME)
    BuiltinValueMappingDefs.EnumMappingDef.map(ElementType.TYPE,        null, null, null, null, null).asInstanceOf[AnyRef] should equal (ElementType.TYPE)
  }

  test("long") {
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typeOf[java.lang.Long],       NoQualifier, typeOf[java.lang.Long], NoQualifier) should equal (true)
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typeOf[java.lang.Integer],    NoQualifier, typeOf[java.lang.Long], NoQualifier) should equal (true)
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typeOf[java.lang.Short],      NoQualifier, typeOf[java.lang.Long], NoQualifier) should equal (true)
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typeOf[java.lang.Byte],       NoQualifier, typeOf[java.lang.Long], NoQualifier) should equal (true)
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typeOf[java.math.BigDecimal], NoQualifier, typeOf[java.lang.Long], NoQualifier) should equal (true)

    BuiltinValueMappingDefs.LongMappingDef.canHandle (typeOf[java.lang.Character], NoQualifier, typeOf[java.lang.Long],    NoQualifier) should equal (false)
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typeOf[java.lang.Long],      NoQualifier, typeOf[java.lang.Integer], NoQualifier) should equal (false)

    BuiltinValueMappingDefs.LongMappingDef.map(1,             null, null, null, null, null) should equal(1L)
    BuiltinValueMappingDefs.LongMappingDef.map(BigDecimal(1), null, null, null, null, null) should equal(1L)
  }

  test("charToString") {
    BuiltinValueMappingDefs.CharToStringMappingDef.canHandle (typeOf[java.lang.Character], NoQualifier, typeOf[java.lang.String],    NoQualifier) should equal (true)
    BuiltinValueMappingDefs.CharToStringMappingDef.canHandle (typeOf[java.lang.Character], NoQualifier, typeOf[java.lang.Character], NoQualifier) should equal (false)
    BuiltinValueMappingDefs.CharToStringMappingDef.canHandle (typeOf[java.lang.String],    NoQualifier, typeOf[java.lang.String],    NoQualifier) should equal (false)

    BuiltinValueMappingDefs.CharToStringMappingDef.map ('a', null, null, null, null, null) should equal ("a")
    BuiltinValueMappingDefs.CharToStringMappingDef.map ('b', null, null, null, null, null) should equal ("b")
  }

  test("stringToChar") {
    BuiltinValueMappingDefs.StringToCharMappingDef.canHandle (typeOf[java.lang.String],    NoQualifier, typeOf[java.lang.Character], NoQualifier) should equal (true)
    BuiltinValueMappingDefs.StringToCharMappingDef.canHandle (typeOf[java.lang.Character], NoQualifier, typeOf[java.lang.Character], NoQualifier) should equal (false)
    BuiltinValueMappingDefs.StringToCharMappingDef.canHandle (typeOf[java.lang.String],    NoQualifier, typeOf[java.lang.String],    NoQualifier) should equal (false)

    BuiltinValueMappingDefs.StringToCharMappingDef.map ("a", null, null, null, null, null) should equal ('a')
    BuiltinValueMappingDefs.StringToCharMappingDef.map ("b", null, null, null, null, null) should equal ('b')
  }
}
