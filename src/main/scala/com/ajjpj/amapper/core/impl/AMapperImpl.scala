package com.ajjpj.amapper.core.impl

import com.ajjpj.amapper.AMapper
import com.ajjpj.amapper.core._
import scala.collection.mutable.ArrayBuffer

/**
 * @author arno
 */
class AMapperImpl[H] (valueMappings: CanHandleSourceAndTargetCache[AValueMappingDef[_,_,_>:H]],
                  objectMappings: CanHandleSourceAndTargetCache[AObjectMappingDef[_,_,_>:H]],
                  log: AMapperLogger = AMapperLogger.defaultLogger,
                  helperFactory: () => H, identifierExtractor: IdentifierExtractor,
                  contextExtractor: AContextExtractor,
                  preProcessor: CanHandleSourceAndTargetCache[APreProcessor],
                  postProcessor: CanHandleSourceAndTargetCache[APostProcessor]
                  ) extends AMapper {
  override def map(source: AnyRef, sourceType: AType, sourceQualifier: AQualifier, target: AnyRef, targetType: AType, targetQualifier: AQualifier): Option[AnyRef] = {
    val deferredWork = ArrayBuffer[()=>Unit]()
    val worker = new AMapperWorkerImpl[H](valueMappings, objectMappings, log, helperFactory(), identifierExtractor, contextExtractor, preProcessor, postProcessor, deferredWork)
    val result = worker.map(new PathBuilder, source, target, QualifiedSourceAndTargetType(sourceType, sourceQualifier, targetType, targetQualifier), Map[String, AnyRef]())

    while(!deferredWork.isEmpty) {
      // This roundabout way of iterating is done to facilitate changes to the 'deferredWork' list inside the loop
      val workItem = deferredWork(0)
      deferredWork.remove(0)
      workItem.apply()
    }

    result
  }
}
