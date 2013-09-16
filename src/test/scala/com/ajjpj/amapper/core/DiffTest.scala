package com.ajjpj.amapper.core

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import com.ajjpj.amapper.javabean.builder.{JavaBeanMapping, JavaBeanMapperBuilder}
import com.ajjpj.amapper.javabean.japi.classes._
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

    val firstNameDiff = diff.getSingle(APath(List(SimplePathSegment("firstName"))))
    firstNameDiff.isDefined should equal (true)
    firstNameDiff.get.oldValue should equal ("Arno")
    firstNameDiff.get.newValue should equal ("Fred")
    firstNameDiff.get.isDerived should equal (true)

    diff.getSingle("firstName") should equal (firstNameDiff)
  }

  //TODO ref with same identifier

  val ie = new IdentifierExtractor {
    def uniqueIdentifier(o: AnyRef, tpe: AType) = o match {
      case x: DiffSource => x.getOid
      case x: DiffTarget => x.getOid
      case x: DiffSourceChild => x.getOid
      case x: DiffTargetChild => x.getOid
    }
  }

  val mapper = JavaBeanMapperBuilder.create()
    .withIdentifierExtractor(ie)
    .addBeanMapping(JavaBeanMapping.create(classOf[DiffSource], classOf[DiffTarget])
      .addMapping("oid", classOf[java.lang.String], "oid", classOf[java.lang.Long])
      .addMapping("sourceName", classOf[String], "targetName", classOf[String])
      .addMapping("sourceChild", classOf[DiffSourceChild], "targetChild", classOf[DiffTargetChild])
    )
    .addBeanMapping(JavaBeanMapping.create(classOf[DiffSourceChild], classOf[DiffTargetChild])
      .addMapping("oid", classOf[java.lang.String], "oid", classOf[java.lang.Long])
      .addMapping("sourceNum", classOf[java.lang.Double], "targetNum", classOf[java.lang.Integer])
    )
    .build

  test("diff equal") {
    val s1 = new DiffSource(1, "source", new DiffSourceChild(2, 123.0))
    val s2 = new DiffSource(1, "source", new DiffSourceChild(2, 123.0))

    val diff = mapper.diff(s1, s2, JavaBeanTypes[DiffSource], NoQualifier, JavaBeanTypes[DiffTarget], NoQualifier)

    diff.isEmpty should equal (true)
  }

  test("diff source different but target equal") {
    val s1 = new DiffSource(1, "source", new DiffSourceChild(2, 123.1))
    val s2 = new DiffSource(1, "source", new DiffSourceChild(2, 123.2))

    val diff = mapper.diff(s1, s2, JavaBeanTypes[DiffSource], NoQualifier, JavaBeanTypes[DiffTarget], NoQualifier)

    diff.isEmpty should equal (true)
  }

  test("diff attribute different") {
    val s1 = new DiffSource(1, "source", new DiffSourceChild(2, 123.1))
    val s2 = new DiffSource(1, "source", new DiffSourceChild(2, 124.1))

    val diff = mapper.diff(s1, s2, JavaBeanTypes[DiffSource], NoQualifier, JavaBeanTypes[DiffTarget], NoQualifier)

    diff.isEmpty should equal (false)
    diff.elements.size should equal (1)

    diff.getSingle("targetChild.targetNum").map(_.oldValue) should equal (Some(123))
    diff.getSingle("targetChild.targetNum").map(_.newValue) should equal (Some(124))
    diff.getSingle("targetChild.targetNum").map(_.isDerived) should equal (Some(false))

    diff.getSingle("targetChild.targetNum").map(_.isInstanceOf[AttributeDiffElement]) should equal (Some(true))
  }

  test("diff ref different") {
    val s1 = new DiffSource(1, "source", new DiffSourceChild(2, 123.1))
    val s2 = new DiffSource(1, "source", new DiffSourceChild(3, 123.1))

    val diff = mapper.diff(s1, s2, JavaBeanTypes[DiffSource], NoQualifier, JavaBeanTypes[DiffTarget], NoQualifier)

    diff.elements.size should equal (2)

    diff.getSingle("targetChild").map(_.oldValue) should equal (Some(2L))
    diff.getSingle("targetChild").map(_.newValue) should equal (Some(3L))
    diff.getSingle("targetChild").map(_.isDerived) should equal (Some(false))
    diff.getSingle("targetChild").map(_.isInstanceOf[ChangeRefDiffElement]) should equal (Some(true))

    diff.getSingle("targetChild.oid").map(_.oldValue) should equal (Some(2L))
    diff.getSingle("targetChild.oid").map(_.newValue) should equal (Some(3L))
    diff.getSingle("targetChild.oid").map(_.isDerived) should equal (Some(true))
    diff.getSingle("targetChild.oid").map(_.isInstanceOf[AttributeDiffElement]) should equal (Some(true))
  }

  test("diff cascade") {
    val s1 = new DiffSource(1, "source1", new DiffSourceChild(3, 123.1))
    val s2 = new DiffSource(2, "source2", new DiffSourceChild(4, 123.1))

    val diff = mapper.diff(s1, s2, JavaBeanTypes[DiffSource], NoQualifier, JavaBeanTypes[DiffTarget], NoQualifier)

    diff.elements.size should equal (5)

    diff.getSingle("").map(_.oldValue) should equal (Some(1L))
    diff.getSingle("").map(_.newValue) should equal (Some(2L))
    diff.getSingle("").map(_.isDerived) should equal (Some(false))

    diff.getSingle("oid").map(_.oldValue) should equal (Some(1L))
    diff.getSingle("oid").map(_.newValue) should equal (Some(2L))
    diff.getSingle("oid").map(_.isDerived) should equal (Some(true))

    diff.getSingle("targetName").map(_.oldValue) should equal (Some("source1"))
    diff.getSingle("targetName").map(_.newValue) should equal (Some("source2"))
    diff.getSingle("targetName").map(_.isDerived) should equal (Some(true))

    diff.getSingle("targetChild").map(_.oldValue) should equal (Some(3L))
    diff.getSingle("targetChild").map(_.newValue) should equal (Some(4L))
    diff.getSingle("targetChild").map(_.isDerived) should equal (Some(true))

    diff.getSingle("targetChild.oid").map(_.oldValue) should equal (Some(3L))
    diff.getSingle("targetChild.oid").map(_.newValue) should equal (Some(4L))
    diff.getSingle("targetChild.oid").map(_.isDerived) should equal (Some(true))
  }

  test("diff from cascade") {
    val mapper = JavaBeanMapperBuilder.create()
      .withIdentifierExtractor(ie)
      .addBeanMapping(JavaBeanMapping.create(classOf[DiffSource], classOf[DiffTarget])
        .addMapping("sourceChild.sourceNum", classOf[java.lang.Double], "derivedTargetNum", classOf[Integer])
      )
      .build

    val s1 = new DiffSource(1, "", new DiffSourceChild(1, 1.0))
    val s2 = new DiffSource(1, "", new DiffSourceChild(1, 2.0))

    val diff = mapper.diff(s1, s2, JavaBeanTypes[DiffSource], NoQualifier, JavaBeanTypes[DiffTarget], NoQualifier)

    diff.elements.size should equal (1)
    diff.getSingle("derivedTargetNum").map(_.oldValue) should equal (Some(1))
    diff.getSingle("derivedTargetNum").map(_.newValue) should equal (Some(2))
    diff.getSingle("derivedTargetNum").map(_.isDerived) should equal (Some(false))
  }

  test("diff to cascade") {
    val mapper = JavaBeanMapperBuilder.create()
      .withIdentifierExtractor(ie)
      .addBeanMapping(JavaBeanMapping.create(classOf[DiffSource], classOf[DiffTarget])
        .addMapping("sourceChild.sourceNum", classOf[java.lang.Double], "derivedTargetNum", classOf[Integer])
      )
      .build

    val s1 = new DiffTarget(1, "", new DiffTargetChild(1, 2))
    val s2 = new DiffTarget(1, "", new DiffTargetChild(1, 2))

    s1.setDerivedTargetNum(1)
    s2.setDerivedTargetNum(2)

    val diff = mapper.diff(s1, s2, JavaBeanTypes[DiffTarget], NoQualifier, JavaBeanTypes[DiffSource], NoQualifier)

    diff.elements.size should equal (1)
    diff.getSingle("sourceChild.sourceNum").map(_.oldValue) should equal (Some(1.0))
    diff.getSingle("sourceChild.sourceNum").map(_.newValue) should equal (Some(2.0))
    diff.getSingle("sourceChild.sourceNum").map(_.isDerived) should equal (Some(false))
    diff.getSingle("sourceChild.sourceNum").map(_.path) should equal (Some(new PathBuilder(List(SimplePathSegment("sourceChild.sourceNum"))).build))
  }

  test("diff ognl") {
    val mapper = JavaBeanMapperBuilder.create()
      .withIdentifierExtractor(ie)
      .addBeanMapping(JavaBeanMapping.create(classOf[DiffSource], classOf[DiffTarget])
        .addMapping("sourceChild.getSourceNum()", classOf[java.lang.Double], "derivedTargetNum", classOf[Integer])
      )
      .build

    val s1 = new DiffSource(1, "", new DiffSourceChild(1, 1.0))
    val s2 = new DiffSource(1, "", new DiffSourceChild(1, 2.0))

    val diff = mapper.diff(s1, s2, JavaBeanTypes[DiffSource], NoQualifier, JavaBeanTypes[DiffTarget], NoQualifier)

    diff.elements.size should equal (1)
    diff.getSingle("derivedTargetNum").map(_.oldValue) should equal (Some(1))
    diff.getSingle("derivedTargetNum").map(_.newValue) should equal (Some(2))
    diff.getSingle("derivedTargetNum").map(_.isDerived) should equal (Some(false))
  }
}
