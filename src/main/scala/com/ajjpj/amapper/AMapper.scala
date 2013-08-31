package com.ajjpj.amapper

import com.ajjpj.amapper.core.{AQualifier, AType}

/**
 * @author arno
 */
trait AMapper {
  def map(source: AnyRef, sourceType: AType, sourceQualifier: AQualifier, target: AnyRef, targetType: AType, targetQualifier: AQualifier): AnyRef
}
