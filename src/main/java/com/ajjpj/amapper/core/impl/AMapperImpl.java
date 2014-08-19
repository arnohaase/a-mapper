package com.ajjpj.amapper.core.impl;

import com.ajjpj.abase.collection.immutable.AHashMap;
import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.abase.function.AFunction0;
import com.ajjpj.abase.function.AStatement0;
import com.ajjpj.amapper.AMapper;
import com.ajjpj.amapper.core.*;
import com.ajjpj.amapper.core.compile.AMappingDefCompiler;
import com.ajjpj.amapper.core.diff.ADiff;
import com.ajjpj.amapper.core.exclog.AMapperLogger;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.core.tpe.AType;
import com.ajjpj.amapper.core.tpe.CanHandleSourceAndTargetCache;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author arno
 */
public class AMapperImpl<H> implements AMapper {
    private static final AMap<String,Object> EMPTY_CONTEXT = AHashMap.empty();

    private final CanHandleSourceAndTargetCache<AObjectMappingDef<?, ?, ? super H>, AObjectMappingDef<Object, Object, H>> objectMappings;
    private final CanHandleSourceAndTargetCache<AValueMappingDef<?, ?, ? super H>, AValueMappingDef<Object, Object, H>> valueMappings;
    private final AMapperLogger logger;
    private final AFunction0<H, RuntimeException> helperFactory;
    private final AIdentifierExtractor identifierExtractor;
    private final AContextExtractor contextExtractor;
    private final CanHandleSourceAndTargetCache<APreProcessor, APreProcessor> preProcessors;
    private final CanHandleSourceAndTargetCache<APostProcessor, APostProcessor> postProcessors;

    public AMapperImpl(Collection<? extends AObjectMappingDef<?, ?, ? super H>> objectMappings,
                       Collection<? extends AValueMappingDef<?, ?, ? super H>> valueMappings,
                       AMapperLogger logger, AFunction0<H, RuntimeException> helperFactory,
                       AIdentifierExtractor identifierExtractor,
                       AContextExtractor contextExtractor,
                       Collection<? extends APreProcessor> preProcessors,
                       Collection<? extends APostProcessor> postProcessors) {
        this.objectMappings = new CanHandleSourceAndTargetCache<>("no object mapping found for ", objectMappings);
        this.valueMappings = new CanHandleSourceAndTargetCache<>("no value mapping found for ", valueMappings);
        this.logger = logger;
        this.helperFactory = helperFactory;
        this.identifierExtractor = identifierExtractor;
        this.contextExtractor = contextExtractor;
        this.preProcessors = new CanHandleSourceAndTargetCache<>("no preprocessor found for ", preProcessors);
        this.postProcessors = new CanHandleSourceAndTargetCache<>("no postprocessor found for ", postProcessors);
    }

    @Override public AOption<Object> map(Object source, AType sourceType, AQualifier sourceQualifier, Object target, AType targetType, AQualifier targetQualifier) {
        final Queue<AStatement0<RuntimeException>> deferredWork = new LinkedList<AStatement0<RuntimeException>>();
        final AMapperWorker<H> worker = new AMapperWorkerImpl<H>(valueMappings, objectMappings, logger, helperFactory.apply(), identifierExtractor, contextExtractor, preProcessors, postProcessors, deferredWork);
        final AOption<Object> result = worker.map(APath.EMPTY, source, target, AQualifiedSourceAndTargetType.create (sourceType, sourceQualifier, targetType, targetQualifier), EMPTY_CONTEXT);

        while(!deferredWork.isEmpty()) {
            deferredWork.remove().apply();
        }

        return result;
    }

    @Override public ADiff diff(Object sourceOld, Object sourceNew, AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) {
        final Queue<AStatement0<RuntimeException>> deferredWork = new LinkedList<>();
        final AMapperDiffWorkerImpl worker = new AMapperDiffWorkerImpl<H>(valueMappings, objectMappings, logger, helperFactory.apply(), identifierExtractor, contextExtractor, preProcessors, deferredWork);
        worker.diff(APath.EMPTY, sourceOld, sourceNew, AQualifiedSourceAndTargetType.create (sourceType, sourceQualifier, targetType, targetQualifier), EMPTY_CONTEXT, EMPTY_CONTEXT, false);

        while(!deferredWork.isEmpty()) {
            deferredWork.remove().apply();
        }

        return worker.getDiffResult();
    }

    /**
     * @return a <em>new</em> instance of AMapper with "compiled" mapping defs
     */
    public AMapperImpl compile() throws Exception {
        final AMappingDefCompiler compiler = new AMappingDefCompiler(logger, objectMappings.getAll(), valueMappings.getAll());

        return new AMapperImpl(compiler.getCompiledObjectMappingDefs(), valueMappings.getAll(), logger, helperFactory, identifierExtractor, contextExtractor, preProcessors.getAll(), postProcessors.getAll());
    }
}

