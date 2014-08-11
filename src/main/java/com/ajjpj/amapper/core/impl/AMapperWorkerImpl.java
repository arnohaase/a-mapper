package com.ajjpj.amapper.core.impl;

import com.ajjpj.abase.collection.immutable.AHashMap;
import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.abase.function.AFunction0;
import com.ajjpj.abase.function.AFunction0NoThrow;
import com.ajjpj.abase.function.AStatement0;
import com.ajjpj.abase.function.AStatement1;
import com.ajjpj.amapper.core.*;
import com.ajjpj.amapper.core.exclog.AMapperExceptionHandler;
import com.ajjpj.amapper.core.exclog.AMapperLogger;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.CanHandleSourceAndTargetCache;

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
    private final Queue<AStatement0<RuntimeException>> deferredWork;

    private final IdentityCache identityCache = new IdentityCache();

    public AMapperWorkerImpl(CanHandleSourceAndTargetCache<AValueMappingDef<?, ?, ? super H>, AValueMappingDef<Object, Object, H>> valueMappings,
                             CanHandleSourceAndTargetCache<AObjectMappingDef<?, ?, ? super H>, AObjectMappingDef<Object, Object, H>> objectMappings,
                             AMapperLogger logger, H helpers,
                             AIdentifierExtractor identifierExtractor, AContextExtractor contextExtractor,
                             CanHandleSourceAndTargetCache<APreProcessor, APreProcessor> preProcessor,
                             CanHandleSourceAndTargetCache<APostProcessor, APostProcessor> postProcessor,
                             Queue<AStatement0<RuntimeException>> deferredWork) {
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

    @Override public AOption<Object> map(APath path, Object source, Object target, AQualifiedSourceAndTargetType types, AMap<String, Object> context) {
        try {
            final AOption<AValueMappingDef<Object, Object, H>> vm = valueMappings.tryEntryFor(types);
            if(vm.isDefined()) {
                return AOption.some(vm.get().map(source, types, this, context));
            }
            else {
                return mapObject(path, source, target, types, context);
            }
        } catch (Exception exc) {
            return AMapperExceptionHandler.onError(exc, path);
        }
    }

    @Override public AOption<Object> mapObject(final APath path, final Object sourceRaw, Object target, AQualifiedSourceAndTargetType types, AMap<String, Object> context) {
        logger.debug (new AFunction0NoThrow<String>() {
            @Override
            public String apply() {
                return "map: " + sourceRaw + " @ " + path;
            }
        });

        try {
            final AObjectMappingDef<Object, Object, H> m = objectMappings.expectedEntryFor(types, path);
            final AOption<Object> preProcessed = preProcess(preProcessors.allEntriesFor(types), sourceRaw, types);

            if(preProcessed.isEmpty()) {
                // preprocessor vetoed this mapping operation
                return AOption.none();
            }

            final Object source = preProcessed.get();

            final AOption<Object> cached = identityCache.get (source, types.target ());
            if(cached.isDefined()) {
                return cached;
            }

            final AMap<String, Object> newContext = contextExtractor.withContext(context, sourceRaw, types.sourceType);
            final Object resultRaw = m.map(source, target, types, this, newContext, path);

            final Object result = postProcess(postProcessors.allEntriesFor(types), resultRaw, types);

            if(m.isCacheable()) {
                identityCache.register(source, result, types.target (), path);
            }

            return AOption.some(result);
        } catch (Exception exc) {
            return AMapperExceptionHandler.onError(exc, path);
        }
    }

    private AOption<Object> preProcess(Iterable<APreProcessor> allPre, Object sourceRaw, AQualifiedSourceAndTargetType types) {
        AOption<Object> result = AOption.some(sourceRaw);
        for(APreProcessor pre: allPre) {
            result = pre.preProcess(result.get(), types);
            if(result.isEmpty()) {
                return result;
            }
        }
        return result;
    }

    private Object postProcess(Iterable<APostProcessor> allPost, Object sourceRaw, AQualifiedSourceAndTargetType types) {
        Object curResult = sourceRaw;
        for(APostProcessor post: allPost) {
            curResult = post.postProcess(curResult, types);
        }
        return curResult;
    }

    @Override public Object mapValue(final APath path, final Object source, AQualifiedSourceAndTargetType types, AMap<String, Object> context) {
        logger.debug (new AFunction0NoThrow<String>() {
            @Override
            public String apply() {
                return "map: " + source + " @ " + path;
            }
        });

        try {
            final AValueMappingDef<Object, Object, H> vm = valueMappings.expectedEntryFor(types, path);
            return vm.map(source, types, this, context);
        }
        catch(Exception exc) {
            return AMapperExceptionHandler.onError(exc, path);
        }
    }

    @Override public void mapDeferred(final APath path, final Object sourceRaw, final AFunction0<Object, Exception> target, final AQualifiedSourceAndTargetType types, final AStatement1<Object, Exception> callback) {
        logger.debug (new AFunction0NoThrow<String>() {
            @Override public String apply() {
                return "map deferred: " + types + " @ " + path;
            }
        });

        deferredWork.add(new AStatement0<RuntimeException>() {
            @Override public void apply() {
                logger.debug(new AFunction0NoThrow<String>() {
                    @Override
                    public String apply() {
                        return "processing deferred: " + types + " @ " + path;
                    }
                });

                try {
                    final AOption<Object> preProcessed = preProcess(preProcessors.allEntriesFor(types), sourceRaw, types);

                    if(preProcessed.isEmpty()) {
                        return;
                    }
                    final Object source = preProcessed.get();

                    final AOption<Object> prevTarget = identityCache.get(source, types.target ());
                    if(prevTarget.isDefined()) {
                        callback.apply(prevTarget.get());
                    }
                    else {
                        logger.deferredWithoutInitial(path); //TODO special treatment for collections etc. --> flag in the mapping def?
                        // create a new, empty context: context is accumulated only from parents to children
                        final AOption<Object> mapped = mapObject(path, source, target.apply (), types, AHashMap.<String, Object>empty());
                        if(mapped.isDefined()) {
                            callback.apply(mapped.get());
                        }
                        //TODO documentation: explain why there is no post processing done here
                    }
                }
                catch (Exception exc) {
                    AMapperExceptionHandler.onError(exc, path);
                }
            }
        });
    }
}
