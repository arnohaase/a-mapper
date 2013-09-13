package com.ajjpj.amapper.core

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class PathTest extends FunSuite with ShouldMatchers {
  test("PathBuilder") {
    val builder = new PathBuilder() + SimplePathSegment("a") + SimplePathSegment("b")
    builder.build should equal (APath(List(SimplePathSegment("a"), SimplePathSegment("b"))))

    val b1 = builder + SimplePathSegment("c1")
    val b2 = builder + SimplePathSegment("c2")
    b1.build should equal (APath(List(SimplePathSegment("a"), SimplePathSegment("b"), SimplePathSegment("c1"))))
    b2.build should equal (APath(List(SimplePathSegment("a"), SimplePathSegment("b"), SimplePathSegment("c2"))))
  }

  test("toString") {
    APath(List(SimplePathSegment("a"), ParameterizedPathSegment("b", "keyB"), IndexedPathSegment("c", 3))).toString should equal ("Path{a.b(keyB).c[3]}")
  }

  test("decompose") {
    val p = APath(List(SimplePathSegment("a"), ParameterizedPathSegment("b", "keyB"), IndexedPathSegment("c", 3)))

    p.last should equal(IndexedPathSegment("c", 3))
    p.parent should equal (APath(List(SimplePathSegment("a"), ParameterizedPathSegment("b", "keyB"))))

    val shortPath = APath(List(SimplePathSegment("a")))
    shortPath.parent should equal (APath(Nil))
    shortPath.last should equal (SimplePathSegment("a"))
  }
}
