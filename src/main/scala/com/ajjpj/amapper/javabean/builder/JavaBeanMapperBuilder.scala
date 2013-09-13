package com.ajjpj.amapper.javabean.builder

import com.ajjpj.amapper.AMapper
import com.ajjpj.amapper.core.impl.AMapperImpl
import com.ajjpj.amapper.core._
import com.ajjpj.amapper.javabean.{AnnotationBasedContextExtractor, BuiltinValueMappingDefs, SimpleBeanMappingHelper, JavaBeanMappingHelper}

/**
 * @author arno
 */
class JavaBeanMapperBuilder[H <: JavaBeanMappingHelper](val helperFactory: () => H) {
  import BuiltinValueMappingDefs._

  var valueMappings: List[AValueMappingDef[_,_,_>:H]] = List(
    StringMappingDef, BooleanMappingDef, DateMappingDef, ClassMappingDef, CharMappingDef, BigDecimalMappingDef, EnumMappingDef,
    ByteMappingDef, ShortMappingDef, IntMappingDef, LongMappingDef, FloatMappingDef, DoubleMappingDef
  )

  var objectMappings: List[AObjectMappingDef[_,_,_>:H]] = List()
  var log: AMapperLogger = AMapperLogger.defaultLogger

  var preProcessor = List[APreProcessor]()
  var postProcessor = List[APostProcessor]()

  var identifierExtractor: IdentifierExtractor = new IdentifierExtractor {
    def uniqueIdentifier(o: AnyRef, tpe: AType) = o.toString
  }
  var contextExtractor: AContextExtractor = AnnotationBasedContextExtractor

  /**
   * later additions take precedence over earlier additions
   */
  def addValueMapping(v: AValueMappingDef[_,_,_>:H]) = {valueMappings = v :: valueMappings; this}

  /**
   * later additions take precedence over earlier additions
   */
  def addObjectMapping(m: AObjectMappingDef[_,_,_>:H]): JavaBeanMapperBuilder[H] = {objectMappings = m :: objectMappings; this}

  def addBeanMapping(m: JavaBeanMapping[_,_]): JavaBeanMapperBuilder[H] = {
    addObjectMapping(m.build)
    if(m.sourceCls != m.targetCls)
      addObjectMapping(m.buildBackward)
    this
  }

  def withLogger(log: AMapperLogger): JavaBeanMapperBuilder[H] = {this.log = log; this}
  def withIdentifierExtractor(identifierExtractor: IdentifierExtractor) = {this.identifierExtractor = identifierExtractor; this}
  def withContextExtractor(contextExtractor: AContextExtractor) = {this.contextExtractor = contextExtractor; this}

  def build: AMapper = new AMapperImpl[H] (new CanHandleSourceAndTargetCache(valueMappings), new CanHandleSourceAndTargetCache(objectMappings), log, helperFactory,
    identifierExtractor, contextExtractor,
    new CanHandleSourceAndTargetCache (preProcessor), new CanHandleSourceAndTargetCache (postProcessor))
}

object JavaBeanMapperBuilder { //TODO remove this
  /**
   * Factory method that allows type inference from Java code
   */
  def create() = new JavaBeanMapperBuilder (() => SimpleBeanMappingHelper)

  /**
   * Factory method that allows type inference from Java code
   */
  def create[H <: JavaBeanMappingHelper] (helperFactory: () => H) = new JavaBeanMapperBuilder (helperFactory)
}