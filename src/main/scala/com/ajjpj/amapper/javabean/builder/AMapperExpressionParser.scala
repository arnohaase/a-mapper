package com.ajjpj.amapper.javabean.builder

import com.ajjpj.amapper.javabean.JavaBeanType
import com.ajjpj.amapper.javabean.propbased._
import com.ajjpj.amapper.javabean.propbased.OgnlPropertyAccessor
import com.ajjpj.amapper.javabean.propbased.MethodPathStep
import com.ajjpj.amapper.javabean.propbased.MethodPathBasedPropertyAccessor
import scala.annotation.tailrec
import com.ajjpj.amapper.core.NoQualifier

/**
 * @author arno
 */
class AMapperExpressionParser (qualifierExtractor: QualifierExtractor) {
  type Type = JavaBeanType[_<:AnyRef]

  def parse(parentClass: Class[_], expression: String, tpe: Type, isDeferred: Boolean): PropertyAccessor = {
    val segments = expression.split("\\.")

    try {
      segments.size match {
        case 1 => JavaBeanSupport.beanProp(parentClass, expression, LiteralIsDeferred(isDeferred), qualifierExtractor).get
        case _ => asPropCascade(parentClass, segments, tpe, isDeferred)
      }
    }
    catch {
      case _: Exception => new OgnlPropertyAccessor(expression, expression, parentClass, isDeferred, tpe, NoQualifier, NoQualifier)
    }
  }

  private def asPropCascade(parentClass: Class[_], segments: Seq[String], tpe: Type, isDeferred: Boolean) = {
    val propName = segments.mkString(".")

    def parseStep(s: String) = s match {
      case _ if s(0) == '?' => (s.substring(1), true)
      case _                => (s,              false)
    }

    @tailrec
    def rec(parentClass: Class[_], segments: List[String], collectedStepsReverse: List[MethodPathStep]): MethodPathBasedPropertyAccessor = segments match {
      case last :: Nil =>
        val (lastProp, nullSafe) = parseStep(last)
        val finalGetter = JavaBeanSupport.asUniqueGetterMethod(parentClass, lastProp)
        val finalSetter = JavaBeanSupport.asUniqueSetterMethod(parentClass, lastProp)

        val getterQualifier = finalGetter.map(qualifierExtractor.extract(_)).getOrElse(NoQualifier)
        val setterQualifier = finalSetter.map(qualifierExtractor.extract(_)).getOrElse(NoQualifier)

        if(finalGetter.isEmpty && finalSetter.isEmpty)
          throw new IllegalArgumentException("no path based property")

        //TODO verify that getter and setter types match
        //TODO verify that getter and setter both match 'tpe'

        MethodPathBasedPropertyAccessor(propName, collectedStepsReverse.reverse, finalGetter, finalSetter, nullSafe, isDeferred, tpe, getterQualifier, setterQualifier)
      case head :: tail =>
        val (stepName, stepNullSafe) = parseStep(head)

        JavaBeanSupport.asUniqueGetterMethod(parentClass, stepName) match {
          case Some(getter) => rec(getter.getReturnType, tail, MethodPathStep(getter, stepNullSafe) :: collectedStepsReverse)
          case None => throw new IllegalArgumentException("new unique getter for step " + head + " in expression " + segments.mkString("."))
        }
    }

    rec(parentClass, segments.toList, Nil)
  }
}
