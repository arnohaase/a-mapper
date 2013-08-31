package com.ajjpj.amapper.collection

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import com.ajjpj.amapper.core._
import org.scalatest.mock.MockitoSugar
import scala.collection.mutable.ArrayBuffer

/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class EquivalenceBasedMergerTest extends FunSuite with ShouldMatchers with MockitoSugar {
  val helper = new ACollectionHelper {
    def createEmptyMutableCollection[T](tpe: AType, sourceTpe: AType): AMutableCollection[T] = ACollectionAdapter (ArrayBuffer[T]())
    def elementType(tpe: AType): AType = null
    def equivalenceMap[S, T](sourceColl: Iterable[S], sourceType: AType, targetColl: Iterable[T], targetType: AType): EquivalenceMap[S, T] = new ComparisonBasedEquivalenceMap[S,T](sourceColl, targetColl, "#"+_ == _)
  }

  var logCountSeveral = 0
  var mapped = Map[String, String]()

  val worker = new AMapperWorker[ACollectionHelper] {
    def logger = new AMapperLogger {
      def debug(msg: => String) {}
      def info(msg: => String) {}
      def warn(msg: String, exc: Exception) {}
      def error(msg: String, exc: Exception) {}

      override def severalExistingTargetsForSource(path: Path, s: AnyRef) {
        logCountSeveral += 1
      }
    }
    def helpers = helper
    def map(path: PathBuilder, source: AnyRef, sourceType: AType, sourceQualifier: AQualifier, target: AnyRef, targetType: AType, targetQualifier: AQualifier) = {mapped += (String.valueOf(source) -> ("#" + source)); "#"+source}
    def mapDeferred(path: PathBuilder, source: AnyRef, sourceType: AType, sourceQualifier: AQualifier, target: => AnyRef, targetType: AType, targetQualifier: AQualifier, callback: (AnyRef) => Unit) = ???
  }

  val merger = new EquivalenceBasedMerger[String, Iterable[String], AMutableCollection[String], String, ACollectionHelper]

  test("simple") {
    val source = List("a", "c", "d")
    val target = scala.collection.mutable.Set("#a", "#b", "#d", "#e")

    val result = merger.map(source, null, NoQualifier, ACollectionAdapter(target), null, NoQualifier, worker, new PathBuilder (Nil))

    target should equal (scala.collection.mutable.Set("#a", "#c", "#d"))
    result.underlying should be theSameInstanceAs target
  }

  test("create new collection") {
    mapped --= mapped.keys
    val result = merger.map(List("a", "bc", "def"), null, NoQualifier, null, null, NoQualifier, worker, new PathBuilder (Nil)).underlying
    result should equal (ArrayBuffer("#a", "#bc", "#def"))
    mapped should equal (Map("a" -> "#a", "bc" -> "#bc", "def" -> "#def"))
  }

  test("remove source duplicates") {
    val result = merger.map(List("a", "b", "a", "b"), null, NoQualifier, null, null, NoQualifier, worker, new PathBuilder(Nil)).underlying
    result should equal (ArrayBuffer("#a", "#b"))
  }

  test("several target equivalents for one source element") {
    logCountSeveral = 0
    val result = merger.map(List("a", "b"), null, NoQualifier, ACollectionAdapter(ArrayBuffer("#a", "#a", "#a", "#b", "#b")), null, NoQualifier, worker, new PathBuilder(Nil)).underlying

    logCountSeveral should equal (2)
    result should equal (ArrayBuffer("#a", "#b"))
  }
}
