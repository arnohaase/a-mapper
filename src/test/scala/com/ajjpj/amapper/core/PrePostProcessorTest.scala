package com.ajjpj.amapper.core

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import com.ajjpj.amapper.core.impl.AMapperImpl
import com.ajjpj.amapper.javabean.BuiltinValueMappingDefs


/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class PrePostProcessorTest extends FunSuite with ShouldMatchers {
  class DataClass {
    var x = "x"
    var y = "unmapped"
    var z = 1
  }

  val type1 = new AType { def name = "type1" }
  val type2 = new AType { def name = "type2" }
  val type3 = new AType { def name = "type3" }
  val type4 = new AType { def name = "type3" }

  val valueMappings = new CanHandleSourceAndTargetCache[AValueMappingDef[_,_,_>:AnyRef]](List(BuiltinValueMappingDefs.StringMappingDef, BuiltinValueMappingDefs.IntMappingDef))
  val objectMapping = new AObjectMappingDef[AnyRef,AnyRef,AnyRef] {
    def canHandle(types: QualifiedSourceAndTargetType) = true
    def map(source: AnyRef, target: AnyRef, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: AnyRef], context: Map[String, AnyRef], path: PathBuilder) = {source.asInstanceOf[DataClass].y="mapped"; source}
  }
  val objectMappings = new CanHandleSourceAndTargetCache[AObjectMappingDef[_,_,_>:AnyRef]](List(objectMapping))

  val preProcessor = new APreProcessor {
    def canHandle(types: QualifiedSourceAndTargetType) = types.targetType == type1 || types.targetType == type2
    def preProcess[T <: AnyRef](o: T, qt: QualifiedSourceAndTargetType) = o match {
      case d: DataClass if qt.sourceType == type1 => d.x="type1"; Some(d.asInstanceOf[T])
      case d: DataClass if qt.sourceType == type2 => d.x="type2"; Some(d.asInstanceOf[T])
      case _ => None
    }
  }

  val postProcessor = new APostProcessor {
    def canHandle(types: QualifiedSourceAndTargetType) = types.targetType == type1 || types.targetType == type3
    def postProcess[T <: AnyRef](o: T, qt: QualifiedSourceAndTargetType) = o match {
      case d: DataClass if qt.sourceType == type1 => d.z+=1; d.asInstanceOf[T]
      case _ => new DataClass().asInstanceOf[T]
    }
  }

  val mapper = new AMapperImpl[AnyRef](valueMappings, objectMappings, AMapperLogger.StdOut, () => null, NoContextExtractor,
    new CanHandleSourceAndTargetCache[APreProcessor](List(preProcessor)),
    new CanHandleSourceAndTargetCache[APostProcessor](List(postProcessor)))

  test("pre and post processor") {
    val orig = new DataClass()
    val mapped = mapper.map(orig, type1, NoQualifier, null, type1, NoQualifier).get.asInstanceOf[DataClass]

    mapped should be theSameInstanceAs(orig)
    mapped.x should equal ("type1")
    mapped.y should equal ("mapped")
    mapped.z should equal (2)
  }

  test ("simple postprocessor") {
    val orig = new DataClass()
    val mapped = mapper.map(orig, type1, NoQualifier, null, type3, NoQualifier).get.asInstanceOf[DataClass]

    mapped should be theSameInstanceAs(orig)
    mapped.x should equal ("x")
    mapped.y should equal ("mapped")
    mapped.z should equal (2)
  }

  test ("simple preprocessor") {
    val orig = new DataClass()
    val mapped = mapper.map(orig, type1, NoQualifier, null, type2, NoQualifier).get.asInstanceOf[DataClass]

    mapped should be theSameInstanceAs(orig)
    mapped.x should equal ("type1")
    mapped.y should equal ("mapped")
    mapped.z should equal (1)
  }

  test ("simple preprocessor 2") {
    val orig = new DataClass()
    val mapped = mapper.map(orig, type2, NoQualifier, null, type2, NoQualifier).get.asInstanceOf[DataClass]

    mapped should be theSameInstanceAs(orig)
    mapped.x should equal ("type2")
    mapped.y should equal ("mapped")
    mapped.z should equal (1)
  }

  test ("no preprocessor") {
    val orig = new DataClass()
    val mapped = mapper.map(orig, type1, NoQualifier, null, type4, NoQualifier).get.asInstanceOf[DataClass]

    mapped should be theSameInstanceAs(orig)
    mapped.x should equal ("x")
    mapped.y should equal ("mapped")
    mapped.z should equal (1)
  }

  test ("preprocessor skip --> post processing is automatically skipped") {
    val orig = new DataClass()
    val oldTarget = new DataClass()
    val mapped = mapper.map(orig, type3, NoQualifier, oldTarget, type1, NoQualifier)

    mapped should equal (None)
  }
}
