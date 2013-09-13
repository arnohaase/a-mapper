package com.ajjpj.amapper.javabean

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import java.util.Date
import java.lang.annotation.{ElementType, RetentionPolicy}
import com.ajjpj.amapper.core.{QualifiedSourceAndTargetType, NoQualifier}
import scala.reflect.ClassTag

/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class BuiltinValueMapingsTest extends FunSuite with ShouldMatchers {
  def typeOf[cls](implicit clsTag: ClassTag[cls]) = SimpleJavaBeanType(clsTag.runtimeClass.asInstanceOf[Class[_<:AnyRef]])
  def typesOf[cls1,cls2](implicit cls1Tag: ClassTag[cls1], cls2Tag: ClassTag[cls2]) = QualifiedSourceAndTargetType(typeOf[cls1], NoQualifier, typeOf[cls2], NoQualifier)

  test("string") {
    BuiltinValueMappingDefs.StringMappingDef.canHandle(typesOf[String,String]) should equal (true)
    BuiltinValueMappingDefs.StringMappingDef.canHandle(typesOf[String,AnyRef]) should equal (false)
    BuiltinValueMappingDefs.StringMappingDef.canHandle(typesOf[AnyRef,String]) should equal (false)
    BuiltinValueMappingDefs.StringMappingDef.canHandle(typesOf[String,Date]  ) should equal (false)
    BuiltinValueMappingDefs.StringMappingDef.canHandle(typesOf[Date,String]  ) should equal (false)

    BuiltinValueMappingDefs.StringMappingDef.map("a",   null, null, null) should equal("a")
    BuiltinValueMappingDefs.StringMappingDef.map("bcd", null, null, null) should equal("bcd")
    BuiltinValueMappingDefs.StringMappingDef.map(null,  null, null, null) should equal(null)
  }

  test("boolean") {
    BuiltinValueMappingDefs.BooleanMappingDef.canHandle (typesOf[java.lang.Boolean,java.lang.Boolean]) should equal (true)
//TODO test this at the 'builder' level BuiltinValueMappingDefs.BooleanMappingDef.canHandle (SimpleJavaBeanType (java.lang.Boolean.TYPE),     SimpleJavaBeanType(java.lang.Boolean.TYPE))     should equal (true)

    BuiltinValueMappingDefs.BooleanMappingDef.map(true,                         null, null, null) should equal(true)
    BuiltinValueMappingDefs.BooleanMappingDef.map(new java.lang.Boolean(true),  null, null, null) should equal(true)
    BuiltinValueMappingDefs.BooleanMappingDef.map(false,                        null, null, null) should equal(false)
    BuiltinValueMappingDefs.BooleanMappingDef.map(new java.lang.Boolean(false), null, null, null) should equal(false)
    BuiltinValueMappingDefs.BooleanMappingDef.map(null,                         null, null, null) should equal(null)
  }

  test("enum") {
    BuiltinValueMappingDefs.EnumMappingDef.canHandle (typesOf[RetentionPolicy,RetentionPolicy]) should equal (true)
    BuiltinValueMappingDefs.EnumMappingDef.canHandle (typesOf[ElementType,    ElementType]    ) should equal (true)
    BuiltinValueMappingDefs.EnumMappingDef.canHandle (typesOf[RetentionPolicy,ElementType]    ) should equal (false)
    BuiltinValueMappingDefs.EnumMappingDef.canHandle (typesOf[ElementType,    RetentionPolicy]) should equal (false)

    BuiltinValueMappingDefs.EnumMappingDef.map(RetentionPolicy.RUNTIME, null, null, null).asInstanceOf[AnyRef] should equal (RetentionPolicy.RUNTIME)
    BuiltinValueMappingDefs.EnumMappingDef.map(ElementType.TYPE,        null, null, null).asInstanceOf[AnyRef] should equal (ElementType.TYPE)
  }

  test("long") {
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typesOf[java.lang.Long,       java.lang.Long]) should equal (true)
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typesOf[java.lang.Integer,    java.lang.Long]) should equal (true)
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typesOf[java.lang.Short,      java.lang.Long]) should equal (true)
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typesOf[java.lang.Byte,       java.lang.Long]) should equal (true)
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typesOf[java.math.BigDecimal, java.lang.Long]) should equal (true)

    BuiltinValueMappingDefs.LongMappingDef.canHandle (typesOf[java.lang.Character, java.lang.Long]   ) should equal (false)
    BuiltinValueMappingDefs.LongMappingDef.canHandle (typesOf[java.lang.Long,      java.lang.Integer]) should equal (false)

    BuiltinValueMappingDefs.LongMappingDef.map(1,             null, null, null) should equal(1L)
    BuiltinValueMappingDefs.LongMappingDef.map(BigDecimal(1), null, null, null) should equal(1L)
  }

  test("charToString") {
    BuiltinValueMappingDefs.CharToStringMappingDef.canHandle (typesOf[java.lang.Character, java.lang.String])    should equal (true)
    BuiltinValueMappingDefs.CharToStringMappingDef.canHandle (typesOf[java.lang.Character, java.lang.Character]) should equal (false)
    BuiltinValueMappingDefs.CharToStringMappingDef.canHandle (typesOf[java.lang.String,    java.lang.String])    should equal (false)

    BuiltinValueMappingDefs.CharToStringMappingDef.map ('a', null, null, null) should equal ("a")
    BuiltinValueMappingDefs.CharToStringMappingDef.map ('b', null, null, null) should equal ("b")
  }

  test("stringToChar") {
    BuiltinValueMappingDefs.StringToCharMappingDef.canHandle (typesOf[java.lang.String,    java.lang.Character]) should equal (true)
    BuiltinValueMappingDefs.StringToCharMappingDef.canHandle (typesOf[java.lang.Character, java.lang.Character]) should equal (false)
    BuiltinValueMappingDefs.StringToCharMappingDef.canHandle (typesOf[java.lang.String,    java.lang.String]   ) should equal (false)

    BuiltinValueMappingDefs.StringToCharMappingDef.map ("a", null, null, null) should equal ('a')
    BuiltinValueMappingDefs.StringToCharMappingDef.map ("b", null, null, null) should equal ('b')
  }
}
