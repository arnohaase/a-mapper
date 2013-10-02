package com.ajjpj.amapper.core2.impl;

import com.ajjpj.amapper.core2.*;
import com.ajjpj.amapper.core2.diff.ADiff;
import com.ajjpj.amapper.core2.exclog.AMapperLogger;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core2.tpe.AQualifier;
import com.ajjpj.amapper.core2.tpe.AType;
import com.ajjpj.amapper.core2.tpe.CanHandleSourceAndTargetCache;
import com.ajjpj.amapper.r2.AMapper;
import com.ajjpj.amapper.util.coll.AHashMap;
import com.ajjpj.amapper.util.coll.AMap;
import com.ajjpj.amapper.util.coll.AOption;
import com.ajjpj.amapper.util.func.AFunction0;
import com.ajjpj.amapper.util.func.AVoidFunction0;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author arno
 */
public class AMapperImpl<H,E extends Exception> implements AMapper {
    private static final AMap<String,Object> EMPTY_CONTEXT = AHashMap.empty();

    private final CanHandleSourceAndTargetCache<AObjectMappingDef<?, ?, ? super H>, AObjectMappingDef<Object, Object, H>> objectMappings;
    private final CanHandleSourceAndTargetCache<AValueMappingDef<?, ?, ? super H>, AValueMappingDef<Object, Object, H>> valueMappings;
    private final AMapperLogger logger;
    private final AFunction0<H, E> helperFactory;
    private final AIdentifierExtractor identifierExtractor;
    private final AContextExtractor contextExtractor;
    private final CanHandleSourceAndTargetCache<APreProcessor, APreProcessor> preProcessors;
    private final CanHandleSourceAndTargetCache<APostProcessor, APostProcessor> postProcessors;

    public AMapperImpl(CanHandleSourceAndTargetCache<AObjectMappingDef<?, ?, ? super H>, AObjectMappingDef<Object, Object, H>> objectMappings, CanHandleSourceAndTargetCache<AValueMappingDef<?, ?, ? super H>, AValueMappingDef<Object, Object, H>> valueMappings, AMapperLogger logger, AFunction0<H, E> helperFactory, AIdentifierExtractor identifierExtractor, AContextExtractor contextExtractor, CanHandleSourceAndTargetCache<APreProcessor, APreProcessor> preProcessors, CanHandleSourceAndTargetCache<APostProcessor, APostProcessor> postProcessors) {
        this.objectMappings = objectMappings;
        this.valueMappings = valueMappings;
        this.logger = logger;
        this.helperFactory = helperFactory;
        this.identifierExtractor = identifierExtractor;
        this.contextExtractor = contextExtractor;
        this.preProcessors = preProcessors;
        this.postProcessors = postProcessors;
    }

    @Override public AOption<Object> map(Object source, AType sourceType, AQualifier sourceQualifier, Object target, AType targetType, AQualifier targetQualifier) throws Exception {
        final Queue<AVoidFunction0<Exception>> deferredWork = new LinkedList<AVoidFunction0<Exception>>();
        final AMapperWorker<H> worker = new AMapperWorkerImpl<H>(valueMappings, objectMappings, logger, helperFactory.apply(), identifierExtractor, contextExtractor, preProcessors, postProcessors, deferredWork);
        final AOption<Object> result = worker.map(APath.EMPTY, source, target, new AQualifiedSourceAndTargetType(sourceType, sourceQualifier, targetType, targetQualifier), EMPTY_CONTEXT);

        while(!deferredWork.isEmpty()) {
            deferredWork.remove().apply();
        }

        return result;
    }

    @Override public ADiff diff(Object sourceOld, Object sourceNew, AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) throws Exception {
        final Queue<AVoidFunction0<Exception>> deferredWork = new LinkedList<AVoidFunction0<Exception>>();
        final AMapperDiffWorkerImpl worker = new AMapperDiffWorkerImpl(valueMappings, objectMappings, logger, helperFactory.apply(), identifierExtractor, contextExtractor, preProcessors, deferredWork);
        worker.diff(APath.EMPTY, sourceOld, sourceNew, new AQualifiedSourceAndTargetType(sourceType, sourceQualifier, targetType, targetQualifier), EMPTY_CONTEXT, EMPTY_CONTEXT, false);

        while(!deferredWork.isEmpty()) {
            deferredWork.remove().apply();
        }

        return worker.getDiffResult();
    }
}

