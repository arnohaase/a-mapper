package com.ajjpj.amapper.core

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import com.ajjpj.amapper.javabean.builder.{JavaBeanMapping, JavaBeanMapperBuilder}
import com.ajjpj.amapper.javabean.japi.classes.{ClassA, ClassB}
import com.ajjpj.amapper.javabean.JavaBeanTypes

/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class DiffTest extends FunSuite with ShouldMatchers {
  test("simple diff") {
    val mapper = JavaBeanMapperBuilder.create()
      .addBeanMapping(JavaBeanMapping.create(classOf[ClassA], classOf[ClassB])
        .addMapping("firstName", classOf[String], "firstName", classOf[String])
        .addMapping("lastName", classOf[String], "lastName", classOf[String]))
      .build

    val a1 = new ClassA
    val a2 = new ClassA

    a1.setFirstName("Arno")
    a1.setLastName("Haase")

    a2.setFirstName("Fred")
    a2.setLastName("Haase")

    val diff = mapper.diff(a1, a2, JavaBeanTypes[ClassA], NoQualifier, JavaBeanTypes[ClassB], NoQualifier)
    diff.elements.size should equal (2)
    diff.paths should equal (Set[APath](APath(Nil), (new PathBuilder + SimplePathSegment("firstName")).build))

//TODO    val firstNameDiff = diff.getSingle("firstName")
    val firstNameDiff = diff.getSingle(APath(List(SimplePathSegment("firstName"))))
    firstNameDiff.isDefined should equal (true)
    firstNameDiff.get.oldValue should equal ("Arno")
    firstNameDiff.get.newValue should equal ("Fred")
    firstNameDiff.get.isDerived should equal (true)
  }

  //TODO ref with same identifier
}
