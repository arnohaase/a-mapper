package com.ajjpj.amapper.core

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import com.ajjpj.amapper.core.impl.IdentityCache

/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class IdentityCacheTest extends FunSuite with ShouldMatchers {
  case class TestKey(s: String)

  test("get and set") {
    val cache = new IdentityCache

    cache.get(TestKey("a")) should equal (None)

    val keyA = TestKey("a")
    val valueA = new AnyRef
    val keyB = TestKey("b")
    val valueB = new AnyRef

    cache.register(keyA, valueA, new PathBuilder(Nil))
    cache.register(keyB, valueB, new PathBuilder(Nil))

    cache.get(keyA).get should be theSameInstanceAs (valueA)
    cache.get(TestKey("a")) should equal (None)

    cache.get(keyB).get should be theSameInstanceAs (valueB)
    cache.get(TestKey("b")) should equal (None)
  }
}
