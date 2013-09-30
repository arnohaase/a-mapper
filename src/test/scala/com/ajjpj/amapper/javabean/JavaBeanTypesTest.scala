package com.ajjpj.amapper.javabean

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import com.ajjpj.amapper.classes.ForTypeTests

/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class JavaBeanTypesTest extends FunSuite with ShouldMatchers {
  test ("simple") {
    SimpleJavaBeanType(classOf[String]).isAssignableFrom(SimpleJavaBeanType(classOf[String])) should equal (true)
    SimpleJavaBeanType(classOf[String]).isAssignableFrom(SimpleJavaBeanType(classOf[AnyRef])) should equal (false)
    SimpleJavaBeanType(classOf[AnyRef]).isAssignableFrom(SimpleJavaBeanType(classOf[String])) should equal (true)

    SimpleJavaBeanType(classOf[String]).cls should be (classOf[String])
  }

  test("single paramCls") {
    SimpleSingleParamBeanType(classOf[java.util.List[_]], classOf[String]).isAssignableFrom(SimpleSingleParamBeanType(classOf[java.util.List[_]],       classOf[String])) should equal(true)
    SimpleSingleParamBeanType(classOf[java.util.List[_]], classOf[String]).isAssignableFrom(SimpleSingleParamBeanType(classOf[java.util.Collection[_]], classOf[String])) should equal(false)
    SimpleSingleParamBeanType(classOf[java.util.List[_]], classOf[String]).isAssignableFrom(SimpleSingleParamBeanType(classOf[java.util.List[_]],       classOf[AnyRef])) should equal(false)

    SimpleSingleParamBeanType(classOf[java.util.Collection[_]], classOf[AnyRef]).isAssignableFrom(SimpleSingleParamBeanType(classOf[java.util.List[_]],       classOf[String])) should equal(true)
    SimpleSingleParamBeanType(classOf[java.util.Collection[_]], classOf[AnyRef]).isAssignableFrom(SimpleSingleParamBeanType(classOf[java.util.Collection[_]], classOf[String])) should equal(true)
    SimpleSingleParamBeanType(classOf[java.util.Collection[_]], classOf[AnyRef]).isAssignableFrom(SimpleSingleParamBeanType(classOf[java.util.List[_]],       classOf[AnyRef])) should equal(true)

    SimpleSingleParamBeanType(classOf[java.util.List[_]], classOf[String]).cls       should equal(classOf[java.util.List[_]])
    SimpleSingleParamBeanType(classOf[java.util.List[_]], classOf[String]).paramCls  should equal(classOf[String])
    SimpleSingleParamBeanType(classOf[java.util.List[_]], classOf[String]).paramType should equal(SimpleJavaBeanType(classOf[String]))
  }

  test("factories") {
    JavaBeanTypes[String]                    should equal (SimpleJavaBeanType(classOf[String]))
    JavaBeanTypes[java.util.List[_], String] should equal (SimpleSingleParamBeanType(classOf[java.util.List[_]], classOf[String]))

    JavaBeanTypes.create(classOf[String])                             should equal (SimpleJavaBeanType(classOf[String]))
    JavaBeanTypes.create(classOf[java.util.List[_]], classOf[String]) should equal (SimpleSingleParamBeanType(classOf[java.util.List[_]], classOf[String]))
  }

  test("java types") {
    val tpeString     = classOf[ForTypeTests].getMethod("withString").    getGenericReturnType
    val tpeStringList = classOf[ForTypeTests].getMethod("withStringList").getGenericReturnType
    val tpeMap        = classOf[ForTypeTests].getMethod("withMap").       getGenericReturnType

    JavaBeanTypes.tpe(Some(tpeString), None, Some(tpeString)) should equal (SimpleJavaBeanType(classOf[String]))

    try {
      JavaBeanTypes.tpe(Some(tpeString), Some(tpeStringList))
      fail("exception expected")
    }
    catch {
      case _: Exception => // expected behavior //TODO use more specific ScalaTest calls
    }

    JavaBeanTypes.create(tpeString)     should equal (SimpleJavaBeanType        (classOf[String]))
    JavaBeanTypes.create(tpeStringList) should equal (SimpleSingleParamBeanType (classOf[java.util.List[_]], classOf[String]))

    JavaBeanTypes.rawType(tpeString)     should equal (classOf[String])
    JavaBeanTypes.rawType(tpeStringList) should equal (classOf[java.util.List[_]])

    // There is no *built-in* special handling of Java types with more than one parameter, especially in generic factories. Using code
    //  is however free to provide their own implementation of JavaBeanType that provide such support
    JavaBeanTypes.create  (tpeMap) should equal (SimpleJavaBeanType (classOf[java.util.Map[_,_]]))
    JavaBeanTypes.rawType (tpeMap) should equal (classOf[java.util.Map[_,_]])
  }

  test("primitives") {
    JavaBeanTypes.primitiveEquivalents.size should equal (8)

    JavaBeanTypes.primitiveEquivalents(classOf[java.lang.Boolean])   should equal (classOf[Boolean])
    JavaBeanTypes.primitiveEquivalents(classOf[java.lang.Character]) should equal (classOf[Char])
    JavaBeanTypes.primitiveEquivalents(classOf[java.lang.Byte])      should equal (classOf[Byte])
    JavaBeanTypes.primitiveEquivalents(classOf[java.lang.Short])     should equal (classOf[Short])
    JavaBeanTypes.primitiveEquivalents(classOf[java.lang.Integer])   should equal (classOf[Int])
    JavaBeanTypes.primitiveEquivalents(classOf[java.lang.Long])      should equal (classOf[Long])
    JavaBeanTypes.primitiveEquivalents(classOf[java.lang.Float])     should equal (classOf[Float])
    JavaBeanTypes.primitiveEquivalents(classOf[java.lang.Double])    should equal (classOf[Double])

    JavaBeanTypes.normalized (classOf[String]) should equal (classOf[String])

    JavaBeanTypes.normalized (classOf[Boolean]) should equal (classOf[java.lang.Boolean])
    JavaBeanTypes.normalized (classOf[Char])    should equal (classOf[java.lang.Character])
    JavaBeanTypes.normalized (classOf[Byte])    should equal (classOf[java.lang.Byte])
    JavaBeanTypes.normalized (classOf[Short])   should equal (classOf[java.lang.Short])
    JavaBeanTypes.normalized (classOf[Int])     should equal (classOf[java.lang.Integer])
    JavaBeanTypes.normalized (classOf[Long])    should equal (classOf[java.lang.Long])
    JavaBeanTypes.normalized (classOf[Float])   should equal (classOf[java.lang.Float])
    JavaBeanTypes.normalized (classOf[Double])  should equal (classOf[java.lang.Double])
  }
}
