package com.ajjpj.amapper.javabean.propbased

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import com.ajjpj.amapper.javabean.builder.{NoQualifierExtractor, DefaultIsDeferredStrategy, JavaBeanMapping}
import com.ajjpj.amapper.javabean.japi.classes.Person
import com.ajjpj.amapper.core.{QualifiedSourceAndTargetType, NoQualifier, AMapperLogger}
import com.ajjpj.amapper.javabean.JavaBeanTypes
import java.text.DateFormat


/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class PropertyBasedMappingDefTest extends FunSuite with ShouldMatchers {
  test("JavaBeanMapping.create") {
    val m = JavaBeanMapping.create(classOf[Person], classOf[Person], DefaultIsDeferredStrategy, AMapperLogger.StdOut, NoQualifierExtractor).build
    m.props.size should equal (2)

    m.canHandle(QualifiedSourceAndTargetType(JavaBeanTypes[Person],     NoQualifier, JavaBeanTypes[Person],     NoQualifier)) should equal (true)
    m.canHandle(QualifiedSourceAndTargetType(JavaBeanTypes[Person],     NoQualifier, JavaBeanTypes[DateFormat], NoQualifier)) should equal (false)
    m.canHandle(QualifiedSourceAndTargetType(JavaBeanTypes[DateFormat], NoQualifier, JavaBeanTypes[Person],     NoQualifier)) should equal (false)
  }
}
