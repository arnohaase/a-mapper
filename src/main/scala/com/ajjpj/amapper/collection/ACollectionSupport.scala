package com.ajjpj.amapper.collection

import com.ajjpj.amapper.core.{AType, AMapperWorker, PathBuilder, ParameterizedPathSegment}

/**
 * @author arno
 */
private[collection] object ACollectionSupport {
  def elementPath(path: PathBuilder, worker: AMapperWorker[_ <: ACollectionHelper], el: AnyRef, elementType: AType): PathBuilder =
    path + ParameterizedPathSegment("elements", worker.helpers.uniqueIdentifier(el, elementType))

}
