package com.ajjpj.amapper.collection

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class EquivalenceMapTest extends FunSuite with ShouldMatchers {
  test("comp based - simple") {
    val source = List("a", "c", "d")
    val target = Set("a", "b", "d", "e")

    val equivMap = new ComparisonBasedEquivalenceMap[String,String] (source, target, _==_)

    equivMap.source should equal (source)
    equivMap.target should equal (target)

    equivMap.sourceWithoutTarget should equal (Set("c"))
    equivMap.targetWithoutSource should equal (List("b", "e"))

    equivMap.sourcesWithTargetEquivalent should equal (Set("a", "d"))
    equivMap.targetEquivalents("a") should equal (Set (TargetEquivalent("a", 1)))
    equivMap.targetEquivalents("d") should equal (Set (TargetEquivalent("d", 1)))
  }

  test("comp based - repeated source value") {
    val source = List("a", "c", "d", "a", "d")
    val target = Set("a", "b", "d", "e")

    val equivMap = new ComparisonBasedEquivalenceMap[String,String] (source, target, _==_)

    equivMap.source should equal (source)
    equivMap.target should equal (target)

    equivMap.sourceWithoutTarget should equal (Set("c"))
    equivMap.targetWithoutSource should equal (List("b", "e"))

    equivMap.sourcesWithTargetEquivalent should equal (Set("a", "d"))
    equivMap.targetEquivalents("a") should equal (Set(TargetEquivalent("a", 1)))
    equivMap.targetEquivalents("d") should equal (Set(TargetEquivalent("d", 1)))
  }

  test("comp based - several targets for the same source") {
    val a2 = new String("a")
    val a3 = new String("a")
    val b2 = new String("b")

    val source = List("a", "b")
    val target = List("a", "b", b2, a2, a3, "a")

    val equivMap = new ComparisonBasedEquivalenceMap[String,String] (source, target, _==_)

    equivMap.sourceWithoutTarget should equal (Set())
    equivMap.targetWithoutSource should equal (List())

    equivMap.sourcesWithTargetEquivalent should equal(Set("a", "b"))
    equivMap.targetEquivalents("a") should equal (Set(TargetEquivalent("a", 2), TargetEquivalent(a2, 1), TargetEquivalent(a3, 1)))
    equivMap.targetEquivalents("b") should equal (Set(TargetEquivalent("b", 1), TargetEquivalent(b2, 1)))
  }

  test("comp based - several unreferenced instances of same element in target collection") {
    val a2 = new String("a")
    val source = List("b", "c")
    val target = List("a", "b", a2, "a")

    val equivMap = new ComparisonBasedEquivalenceMap[String,String] (source, target, _==_)

    equivMap.sourceWithoutTarget should equal (Set("c"))
    equivMap.targetWithoutSource should equal (List("a", a2, "a"))
  }

  test("comp based - transform") {
    val source = List("a", "b", "cd", "efg", "hi")
    val target = Set(1, 3, 4)

    val equivMap = new ComparisonBasedEquivalenceMap[String,Int] (source, target, (s:String, t:Int) => s.length == t)

    equivMap.source should equal (source)
    equivMap.target should equal (target)

    equivMap.sourceWithoutTarget should equal (Set("cd", "hi"))
    equivMap.targetWithoutSource should equal (List(4))

    equivMap.sourcesWithTargetEquivalent should equal (Set("a", "b", "efg"))
    equivMap.targetEquivalents("a") should equal (Set(TargetEquivalent(1, 1)))
    equivMap.targetEquivalents("b") should equal (Set(TargetEquivalent(1, 1)))
    equivMap.targetEquivalents("efg") should equal (Set(TargetEquivalent(3, 1)))
  }
}

