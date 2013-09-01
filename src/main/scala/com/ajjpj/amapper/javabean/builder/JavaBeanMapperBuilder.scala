package com.ajjpj.amapper.javabean.builder

import com.ajjpj.amapper.AMapper
import com.ajjpj.amapper.core.impl.{AMapperImpl}
import com.ajjpj.amapper.core._
import com.ajjpj.amapper.javabean.{AnnotationBasedContextExtractor, BuiltinValueMappingDefs, SimpleBeanMappingHelper, JavaBeanMappingHelper}
import com.ajjpj.amapper.util.CanHandleCache

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
  var deProxyStrategy: AnyRef => AnyRef = x=>x

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
  def withDeProxyStrategy(deProxyStrategy: AnyRef => AnyRef) = {this.deProxyStrategy = deProxyStrategy; this}
  def withContextExtractor(contextExtractor: AContextExtractor) = {this.contextExtractor = contextExtractor; this}

  def build: AMapper = new AMapperImpl[H] (new CanHandleCache(valueMappings), new CanHandleCache(objectMappings), log, helperFactory, contextExtractor, deProxyStrategy)
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