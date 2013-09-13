package com.ajjpj.amapper

import com.ajjpj.amapper.core.{ADiff, AQualifier, AType}

/**
 * @author arno
 */
trait AMapper {
  def map(source: AnyRef, sourceType: AType, sourceQualifier: AQualifier, target: AnyRef, targetType: AType, targetQualifier: AQualifier): Option[AnyRef]
  def diff(sourceOld: AnyRef, sourceNew: AnyRef, sourceType: AType, sourceQualifier: AQualifier, targetType: AType, targetQualifier: AQualifier): ADiff
}
