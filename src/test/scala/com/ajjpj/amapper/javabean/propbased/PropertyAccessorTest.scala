package com.ajjpj.amapper.javabean.propbased

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import com.ajjpj.amapper.javabean.JavaBeanTypes
import com.ajjpj.amapper.javabean.builder.{NoQualifierExtractor, AMapperExpressionParser}
import com.ajjpj.amapper.core.NoQualifier
import com.ajjpj.amapper.classes.{WithProperties, PartOfPropPath}

/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class PropertyAccessorTest extends FunSuite with ShouldMatchers {
  val cls = classOf[WithProperties]
  val otherCls = classOf[PartOfPropPath]

  def testSimpleAccessor(acc: PropertyAccessor, propName: String = "propName", auxSetter: Option[(WithProperties, String) => Unit] = None) = {
    acc.name should equal (propName)
    acc.isReadable should equal (true)

    auxSetter match {
      case None =>
        acc.isWritable should equal (true)

        val o = new WithProperties
        o.inner.inner = o

        acc.get(o) should equal (null)
        acc.set(o, "xyz")
        acc.get(o) should equal ("xyz")
      case Some(setter) =>
        acc.isWritable should equal (false)

        val o = new WithProperties
        o.inner.inner = o

        acc.get(o) should equal (null)
        setter(o, "xyz")
        acc.get(o) should equal ("xyz")
    }
  }

  def testPathAccessor(acc: PropertyAccessor, propName: String = "propName", firstNullSafe: Boolean, secondNullSafe: Boolean, lastNullSafe: Boolean) = {
    testSimpleAccessor(acc, propName = propName)

    val o = new WithProperties
    if(lastNullSafe)
      acc.get(o) should equal (null)
    else
      evaluating (acc.get(o)) should produce [Exception]

    o.inner = null
    if(secondNullSafe)
      acc.get(o) should equal (null)
    else
      evaluating (acc.get(o)) should produce [Exception]

    if(firstNullSafe)
      acc.get(null) should equal (null)
    else
      evaluating (acc.get(null)) should produce [Exception]
  }

  test ("field") {
    testSimpleAccessor (FieldBasedPropertyAccessor("propName", cls.getDeclaredField("theString"), isDeferred=true, JavaBeanTypes[String], NoQualifier, NoQualifier))
  }

  test ("method") {
    testSimpleAccessor (MethodBasedPropertyAccessor("propName", Some(cls.getDeclaredMethod("getAbc")), Some(cls.getDeclaredMethod("setAbc", classOf[String])), isDeferred=true, JavaBeanTypes[String], NoQualifier, NoQualifier))

    MethodBasedPropertyAccessor("a", Some(cls.getDeclaredMethod("getAbc")), None,                  isDeferred=true, JavaBeanTypes[String], NoQualifier, NoQualifier).isWritable should equal (false)
    MethodBasedPropertyAccessor("a", None, Some(cls.getDeclaredMethod("setAbc", classOf[String])), isDeferred=true, JavaBeanTypes[String], NoQualifier, NoQualifier).isReadable should equal (false)
  }

  test ("ognl") {
    testSimpleAccessor (OgnlPropertyAccessor("propName", "xyz", classOf[WithProperties], isDeferred=true, JavaBeanTypes[String], NoQualifier, NoQualifier))

    OgnlPropertyAccessor("propName", "xyz", classOf[WithProperties], isDeferred=true, JavaBeanTypes[String], NoQualifier, NoQualifier).isWritable should equal (true)
    OgnlPropertyAccessor("readOnly", "readOnly", classOf[WithProperties], isDeferred=true, JavaBeanTypes[String], NoQualifier, NoQualifier).isWritable should equal (false)
  }

  test ("method path") {
    val steps = List(MethodPathStep(cls.getDeclaredMethod("getOther"), nullSafe=false), MethodPathStep(otherCls.getDeclaredMethod("getWithProperties"), nullSafe=false))
    val acc = MethodPathBasedPropertyAccessor("propName", steps, Some(cls.getDeclaredMethod("getAbc")), Some(cls.getDeclaredMethod("setAbc", classOf[String])), finalStepNullSafe = false, isDeferred = false, JavaBeanTypes[String], NoQualifier, NoQualifier)

    testPathAccessor(acc, firstNullSafe = false, secondNullSafe = false, lastNullSafe = false)

    //TODO test readable / writable
  }

  test ("method path first segment null safe") {
    val steps = List(MethodPathStep(cls.getDeclaredMethod("getOther"), nullSafe=true), MethodPathStep(otherCls.getDeclaredMethod("getWithProperties"), nullSafe=false))
    val acc = MethodPathBasedPropertyAccessor("propName", steps, Some(cls.getDeclaredMethod("getAbc")), Some(cls.getDeclaredMethod("setAbc", classOf[String])), finalStepNullSafe = false, isDeferred = false, JavaBeanTypes[String], NoQualifier, NoQualifier)

    testPathAccessor(acc, firstNullSafe = true, secondNullSafe = false, lastNullSafe = false)
  }

  test ("method path middle segment null safe") {
    val steps = List(MethodPathStep(cls.getDeclaredMethod("getOther"), nullSafe=false), MethodPathStep(otherCls.getDeclaredMethod("getWithProperties"), nullSafe=true))
    val acc = MethodPathBasedPropertyAccessor("propName", steps, Some(cls.getDeclaredMethod("getAbc")), Some(cls.getDeclaredMethod("setAbc", classOf[String])), finalStepNullSafe = false, isDeferred = false, JavaBeanTypes[String], NoQualifier, NoQualifier)

    testPathAccessor(acc, firstNullSafe = false, secondNullSafe = true, lastNullSafe = false)
  }

  test ("method path last segment null safe") {
    val steps = List(MethodPathStep(cls.getDeclaredMethod("getOther"), nullSafe=false), MethodPathStep(otherCls.getDeclaredMethod("getWithProperties"), nullSafe=false))
    val acc = MethodPathBasedPropertyAccessor("propName", steps, Some(cls.getDeclaredMethod("getAbc")), Some(cls.getDeclaredMethod("setAbc", classOf[String])), finalStepNullSafe = true, isDeferred = false, JavaBeanTypes[String], NoQualifier, NoQualifier)

    testPathAccessor(acc, firstNullSafe = false, secondNullSafe = false, lastNullSafe = true)
    //TODO test null safety of setter
  }

  test ("parsed method") {
    val acc = new AMapperExpressionParser(NoQualifierExtractor).parse(cls, "theString", JavaBeanTypes[String], isDeferred = false)
    testSimpleAccessor(acc, propName="theString")
  }

  test ("parsed ognl") {
    val acc = new AMapperExpressionParser(NoQualifierExtractor).parse(cls, "1>0?theString:null", JavaBeanTypes[String], isDeferred = false)
    testSimpleAccessor(acc, propName="1>0?theString:null", auxSetter = None)
  }

  test ("parsed path as ognl") {
    val acc = new AMapperExpressionParser(NoQualifierExtractor).parse(cls, "theString.substring(0)", JavaBeanTypes[String], isDeferred = false)
    acc.isInstanceOf[OgnlPropertyAccessor] should equal (true)
  }

  test ("parsed path") {
    val acc = new AMapperExpressionParser(NoQualifierExtractor).parse(cls, "other.withProperties.theString", JavaBeanTypes[String], isDeferred = false)
    testPathAccessor(acc, propName="other.withProperties.theString", firstNullSafe = false, secondNullSafe = false, lastNullSafe = false)
  }

  test ("parsed path null-safe first") {
    val acc = new AMapperExpressionParser(NoQualifierExtractor).parse(cls, "?other.withProperties.theString", JavaBeanTypes[String], isDeferred = false)
    testPathAccessor(acc, propName="?other.withProperties.theString", firstNullSafe = true, secondNullSafe = false, lastNullSafe = false)
  }

  test ("parsed path null-safe second") {
    val acc = new AMapperExpressionParser(NoQualifierExtractor).parse(cls, "other.?withProperties.theString", JavaBeanTypes[String], isDeferred = false)
    testPathAccessor(acc, propName="other.?withProperties.theString", firstNullSafe = false, secondNullSafe = true, lastNullSafe = false)
  }

  test ("parsed path null-safe last") {
    val acc = new AMapperExpressionParser(NoQualifierExtractor).parse(cls, "other.withProperties.?theString", JavaBeanTypes[String], isDeferred = false)
    testPathAccessor(acc, propName="other.withProperties.?theString", firstNullSafe = false, secondNullSafe = false, lastNullSafe = true)
  }
}
