package com.ajjpj.amapper.javabean.builder

import com.ajjpj.amapper.AMapper
import com.ajjpj.amapper.core.impl.{AMapperImpl, MappingDefResolver}
import com.ajjpj.amapper.core.{AMapperLogger, AObjectMappingDef, AValueMappingDef}
import com.ajjpj.amapper.javabean.{BuiltinValueMappingDefs, SimpleBeanMappingHelper, JavaBeanMappingHelper}

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

  /**
   * later additions take precedence over earlier additions
   */
  def addValueMapping(v: AValueMappingDef[_,_,_>:H]) = {valueMappings = v :: valueMappings; this}

  /**
   * later additions take precedence over earlier additions
   */
  def addObjectMapping(m: AObjectMappingDef[_,_,_>:H]): JavaBeanMapperBuilder[H] = {objectMappings = m :: objectMappings; this}

  def addBeanMapping(m: JavaBeanMapping[_,_]): JavaBeanMapperBuilder[H] = {addObjectMapping(m.build); addObjectMapping(m.buildBackward); this}

  def withLogger(log: AMapperLogger): JavaBeanMapperBuilder[H] = {this.log = log; this}
  def withDeProxyStrategy(deProxyStrategy: AnyRef => AnyRef) = {this.deProxyStrategy = deProxyStrategy; this}

  def build: AMapper = new AMapperImpl[H] (new MappingDefResolver(valueMappings), new MappingDefResolver(objectMappings), log, helperFactory, deProxyStrategy)
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