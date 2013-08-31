package com.ajjpj.amapper.core.impl

import com.ajjpj.amapper.AMapper
import com.ajjpj.amapper.core._
import scala.collection.mutable.ArrayBuffer
import com.ajjpj.amapper.util.CanHandleCache

/**
 * @author arno
 */
class AMapperImpl[H] (valueMappings: CanHandleCache[AValueMappingDef[_,_,_>:H]],
                  objectMappings: CanHandleCache[AObjectMappingDef[_,_,_>:H]],
                  log: AMapperLogger = AMapperLogger.defaultLogger,
                  helperFactory: () => H,
                  contextExtractor: AContextExtractor,
                  deProxyStrategy: AnyRef => AnyRef = x=>x) extends AMapper {
  override def map(source: AnyRef, sourceType: AType, sourceQualifier: AQualifier, target: AnyRef, targetType: AType, targetQualifier: AQualifier): AnyRef = {
    var deferredWork = ArrayBuffer[()=>Unit]()
    val worker = new AMapperWorkerImpl[H](valueMappings, objectMappings, log, deProxyStrategy, helperFactory(), contextExtractor, deferredWork)
    val result = worker.map(new PathBuilder, source, sourceType, sourceQualifier, target, targetType, targetQualifier, Map[String, AnyRef]())

    while(!deferredWork.isEmpty) {
      // This roundabout way of iterating is done to facilitate changes to the 'deferredWork' list inside the loop
      val workItem = deferredWork(0)
      deferredWork.remove(0)
      workItem.apply()
    }

    result
  }
}
