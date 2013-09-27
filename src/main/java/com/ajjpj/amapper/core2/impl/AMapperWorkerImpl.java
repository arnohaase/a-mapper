package com.ajjpj.amapper.core2.impl;

import com.ajjpj.amapper.core2.*;
import com.ajjpj.amapper.core2.exclog.AMapperLogger;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core2.tpe.CanHandleSourceAndTargetCache;
import com.ajjpj.amapper.util.coll.AHashMap;
import com.ajjpj.amapper.util.coll.AMap;
import com.ajjpj.amapper.util.coll.AOption;
import com.ajjpj.amapper.util.func.AFunction0;
import com.ajjpj.amapper.util.func.AStringFunction0;
import com.ajjpj.amapper.util.func.AVoidFunction0;
import com.ajjpj.amapper.util.func.AVoidFunction1;

import java.util.Queue;

/**
 * @author arno
 */
public class AMapperWorkerImpl<H> implements AMapperWorker<H> {
    private final CanHandleSourceAndTargetCache<AValueMappingDef<?,?,? super H>, AValueMappingDef<Object, Object, H>> valueMappings;
    private final CanHandleSourceAndTargetCache<AObjectMappingDef<?,?,? super H>, AObjectMappingDef<Object, Object, H>> objectMappings;
    private final AMapperLogger logger;
    private final H helpers;
    private final AIdentifierExtractor identifierExtractor;
    private final AContextExtractor contextExtractor;
    private final CanHandleSourceAndTargetCache<APreProcessor, APreProcessor> preProcessors;
    private final CanHandleSourceAndTargetCache<APostProcessor, APostProcessor> postProcessors;
    private final Queue<AVoidFunction0<Exception>> deferredWork;

    private final IdentityCache identityCache = new IdentityCache();

    public AMapperWorkerImpl(CanHandleSourceAndTargetCache<AValueMappingDef<?, ?, ? super H>, AValueMappingDef<Object, Object, H>> valueMappings,
                             CanHandleSourceAndTargetCache<AObjectMappingDef<?, ?, ? super H>, AObjectMappingDef<Object, Object, H>> objectMappings,
                             AMapperLogger logger, H helpers,
                             AIdentifierExtractor identifierExtractor, AContextExtractor contextExtractor,
                             CanHandleSourceAndTargetCache<APreProcessor, APreProcessor> preProcessor,
                             CanHandleSourceAndTargetCache<APostProcessor, APostProcessor> postProcessor,
                             Queue<AVoidFunction0<Exception>> deferredWork) {
        this.valueMappings = valueMappings;
        this.objectMappings = objectMappings;
        this.logger = logger;
        this.helpers = helpers;
        this.identifierExtractor = identifierExtractor;
        this.contextExtractor = contextExtractor;
        this.preProcessors = preProcessor;
        this.postProcessors = postProcessor;
        this.deferredWork = deferredWork;
    }

    @Override public AMapperLogger getLogger() {
        return logger;
    }

    @Override public H getHelpers() {
        return helpers;
    }

    @Override public AIdentifierExtractor getIdentifierExtractor() {
        return identifierExtractor;
    }

    @Override public AOption<Object> map(APath path, Object source, Object target, AQualifiedSourceAndTargetType types, AMap<String, Object> context) throws Exception {
        final AOption<AValueMappingDef<Object, Object, H>> vm = valueMappings.tryEntryFor(types);
        if(vm.isDefined()) {
            return AOption.some(vm.get().map(source, types, this, context));
        }
        else {
            return mapObject(path, source, target, types, context);
        }
    }

    @Override public AOption<Object> mapObject(final APath path, final Object sourceRaw, Object target, AQualifiedSourceAndTargetType types, AMap<String, Object> context) throws Exception {
        logger.debug (new AStringFunction0() {
            @Override
            public String apply() {
                return "map: " + sourceRaw + " @ " + path;
            }
        });

        final AObjectMappingDef<Object, Object, H> m = objectMappings.expectedEntryFor(types, path);
        final APreProcessor pre = preProcessors.tryEntryFor(types).getOrElse(APreProcessor.NO_PREPROCESSOR);
        final AOption<Object> preProcessed = pre.preProcess(sourceRaw, types);

        if(preProcessed.isEmpty()) {
            // preprocessor vetoed this mapping operation
            return AOption.none();
        }

        final Object source = preProcessed.get();
        final AMap<String, Object> newContext = contextExtractor.withContext(context, sourceRaw, types.sourceType);
        final Object resultRaw = m.map(source, target, types, this, newContext, path);

        final Object result = postProcessors.tryEntryFor(types).getOrElse(APostProcessor.NO_POSTPROCESSOR).postProcess(resultRaw, types);

        if(m.isCacheable()) {
            identityCache.register(source, result, path);
        }

        return AOption.some(result);
    }

    @Override public Object mapValue(final APath path, final Object source, AQualifiedSourceAndTargetType types, AMap<String, Object> context) {
        logger.debug (new AStringFunction0() {
            @Override
            public String apply() {
                return "map: " + source + " @ " + path;
            }
        });
        final AValueMappingDef<Object, Object, H> vm = valueMappings.expectedEntryFor(types, path);
        return vm.map(source, types, this, context);
    }

    @Override public void mapDeferred(final APath path, final Object sourceRaw, final AFunction0<Object, Exception> target, final AQualifiedSourceAndTargetType types, final AVoidFunction1<Object, Exception> callback) {
        logger.debug (new AStringFunction0() {
            @Override public String apply() {
                return "map deferred: " + types + " @ " + path;
            }
        });

        deferredWork.add(new AVoidFunction0<Exception>() {
            @Override public void apply() throws Exception {
                logger.debug(new AStringFunction0() {
                    @Override
                    public String apply() {
                        return "processing deferred: " + types + " @ " + path;
                    }
                });

                final APreProcessor pre = preProcessors.tryEntryFor(types).getOrElse(APreProcessor.NO_PREPROCESSOR);
                final AOption<Object> preProcessed = pre.preProcess(sourceRaw, types);

                if(preProcessed.isEmpty()) {
                    return;
                }
                final Object source = preProcessed.get();

                final AOption<Object> prevTarget = identityCache.get(source);
                if(prevTarget.isDefined()) {
                    callback.apply(prevTarget.get());
                }
                else {
                    logger.deferredWithoutInitial(path); //TODO special treatment for collections etc. --> flag in the mapping def?
                    // create a new, empty context: context is accumulated only from parents to children
                    final AOption<Object> mapped = mapObject(path, source, target, types, AHashMap.<String, Object>empty());
                    if(mapped.isDefined()) {
                        callback.apply(mapped.get());
                    }
                }
            }
        });
    }
}
