package com.ajjpj.amapper.core.impl

import com.ajjpj.amapper.AMapper
import com.ajjpj.amapper.core._
import scala.collection.mutable.ArrayBuffer

/**
 * @author arno
 */
class AMapperImpl[H] (valueMappings: MappingDefResolver[AValueMappingDef[_,_,_>:H]],
                  objectMappings: MappingDefResolver[AObjectMappingDef[_,_,_>:H]],
                  log: AMapperLogger = AMapperLogger.defaultLogger,
                  helperFactory: () => H,
                  deProxyStrategy: AnyRef => AnyRef = x=>x) extends AMapper {
  override def map(source: AnyRef, sourceType: AType, sourceQualifier: AQualifier, target: AnyRef, targetType: AType, targetQualifier: AQualifier): AnyRef = {
    var deferredWork = ArrayBuffer[()=>Unit]()
    val worker = new AMapperWorkerImpl[H](valueMappings, objectMappings, log, deProxyStrategy, helperFactory(), deferredWork)
    val result = worker.map(new PathBuilder, source, sourceType, sourceQualifier, target, targetType, targetQualifier)

    while(!deferredWork.isEmpty) {
      // This roundabout way of iterating is done to facilitate changes to the 'deferredWork' list inside the loop
      val workItem = deferredWork(0)
      deferredWork.remove(0)
      workItem.apply()
    }

    result
  }
}
