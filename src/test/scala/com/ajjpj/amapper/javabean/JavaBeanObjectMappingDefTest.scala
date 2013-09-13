package com.ajjpj.amapper.javabean

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.util.Date
import com.ajjpj.amapper.core._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito

/**
 * This test uses the java bean mapping def superclasses in a nonsensical way - they are object mappings after all.
 *  The reason 'String' and other value types are used is that those types are readily available in the standard
 *  library.
 *
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class JavaBeanObjectMappingDefTest extends FunSuite with ShouldMatchers with MockitoSugar {
  test("abstract") {
    val md = new AbstractJavaBeanObjectMappingDef[String, Date]() {
      override def map(source: String, target: Date, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder): Date = null
      override def diff(diff: ADiffBuilder, sourceOld: String, sourceNew: String, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: JavaBeanMappingHelper], contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], path: PathBuilder, isDerived: Boolean) {}
    }

    md.sourceClass should equal (classOf[String])
    md.targetClass should equal (classOf[Date])

    md.canHandle (QualifiedSourceAndTargetType(SimpleJavaBeanType(classOf[String]), NoQualifier, SimpleJavaBeanType(classOf[Date]), NoQualifier))               should equal (true)
    md.canHandle (QualifiedSourceAndTargetType(SimpleJavaBeanType(classOf[AnyRef]), NoQualifier, SimpleJavaBeanType(classOf[Date]), NoQualifier))               should equal (false)
    md.canHandle (QualifiedSourceAndTargetType(SimpleJavaBeanType(classOf[String]), NoQualifier, SimpleJavaBeanType(classOf[AnyRef]), NoQualifier))             should equal (false)
    md.canHandle (QualifiedSourceAndTargetType(SimpleJavaBeanType(classOf[String]), NoQualifier, SimpleJavaBeanType(classOf[java.sql.Timestamp]), NoQualifier)) should equal (false)
  }

  test("simple") {
    val md = new SimpleJavaBeanObjectMappingDefBase[String, StringBuilder]() {
      def doMap(source: String, target: StringBuilder, worker: AMapperWorker[_ <: JavaBeanMappingHelper], context: Map[String, AnyRef], path: PathBuilder) {
        target.append(source)
      }

      def diff(diff: ADiffBuilder, sourceOld: String, sourceNew: String, types: QualifiedSourceAndTargetType, worker: AMapperWorker[_ <: JavaBeanMappingHelper], contextOld: Map[String, AnyRef], contextNew: Map[String, AnyRef], path: PathBuilder, isDerived: Boolean) {}
    }

    md.sourceClass should equal (classOf[String])
    md.targetClass should equal (classOf[StringBuilder])

    md.canHandle (QualifiedSourceAndTargetType(SimpleJavaBeanType(classOf[String]), NoQualifier, SimpleJavaBeanType(classOf[StringBuilder]), NoQualifier)) should equal (true)
    md.canHandle (QualifiedSourceAndTargetType(SimpleJavaBeanType(classOf[AnyRef]), NoQualifier, SimpleJavaBeanType(classOf[StringBuilder]), NoQualifier)) should equal (false)
    md.canHandle (QualifiedSourceAndTargetType(SimpleJavaBeanType(classOf[String]), NoQualifier, SimpleJavaBeanType(classOf[AnyRef]),        NoQualifier))        should equal (false)

    val prevSb = new StringBuilder
    val mappedPrev = md.map("abc", prevSb, null, null, null, null)
    mappedPrev should be theSameInstanceAs prevSb
    mappedPrev.toString should equal ("abc")

    val worker = mock[AMapperWorker[JavaBeanMappingHelper]]
    import Mockito._
    when (worker.helpers) thenReturn SimpleBeanMappingHelper

    val mappedNew = md.map("xyz", null, QualifiedSourceAndTargetType(null, NoQualifier, JavaBeanTypes[StringBuilder], NoQualifier), worker, null, null)
    mappedNew.getClass should be (classOf[StringBuilder])
    mappedNew.toString should equal ("xyz")
  }
}
