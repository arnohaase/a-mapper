package com.ajjpj.amapper.core.impl;

import com.ajjpj.abase.collection.AEquality;
import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.abase.function.AFunction0NoThrow;
import com.ajjpj.abase.function.AStatement0;
import com.ajjpj.amapper.core.*;
import com.ajjpj.amapper.core.diff.ADiff;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.diff.ADiffElement;
import com.ajjpj.amapper.core.exclog.AMapperExceptionHandler;
import com.ajjpj.amapper.core.exclog.AMapperLogger;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.CanHandleSourceAndTargetCache;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * @author arno
 */
public class AMapperDiffWorkerImpl<H> implements AMapperDiffWorker<H> {
    private final CanHandleSourceAndTargetCache<AValueMappingDef<?,?,? super H>, AValueMappingDef<Object, Object, H>> valueMappings;
    private final CanHandleSourceAndTargetCache<AObjectMappingDef<?,?,? super H>, AObjectMappingDef<Object, Object, H>> objectMappings;
    private final AMapperLogger logger;
    private final H helpers;
    private final AIdentifierExtractor identifierExtractor;
    private final AContextExtractor contextExtractor;
    private final CanHandleSourceAndTargetCache<APreProcessor, APreProcessor> preProcessor;
    private final Queue<AStatement0<RuntimeException>>  deferredWork;

    private final Set<IdentityPair> identityCache = new HashSet<IdentityPair>();
    private final ADiffBuilder diffBuilder = new ADiffBuilder();

    public AMapperDiffWorkerImpl(CanHandleSourceAndTargetCache<AValueMappingDef<?, ?, ? super H>, AValueMappingDef<Object, Object, H>> valueMappings,
                                 CanHandleSourceAndTargetCache<AObjectMappingDef<?, ?, ? super H>, AObjectMappingDef<Object, Object, H>> objectMappings,
                                 AMapperLogger logger, H helpers,
                                 AIdentifierExtractor identifierExtractor, AContextExtractor contextExtractor,
                                 CanHandleSourceAndTargetCache<APreProcessor, APreProcessor> preProcessor,
                                 Queue<AStatement0<RuntimeException>> deferredWork) {
        this.valueMappings = valueMappings;
        this.objectMappings = objectMappings;
        this.logger = logger;
        this.helpers = helpers;
        this.identifierExtractor = identifierExtractor;
        this.contextExtractor = contextExtractor;
        this.preProcessor = preProcessor;
        this.deferredWork = deferredWork;
    }

    public ADiff getDiffResult() {
        return diffBuilder.build();
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

    @Override public void diff(APath path, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> contextOld, AMap<String, Object> contextNew, boolean isDerived) {
        try {
            final AOption<AValueMappingDef<Object, Object, H>> vm = valueMappings.tryEntryFor(types);
            if(vm.isDefined()) {
                vm.get().diff(diffBuilder, sourceOld, sourceNew, types, this, contextOld, contextNew, path, isDerived);
            }
            else {
                diffObject(path, sourceOld, sourceNew, types, contextOld, contextNew, isDerived);
            }
        } catch (Exception exc) {
            AMapperExceptionHandler.onError(exc, path);
        }
    }

    @Override public void diffValue(final APath path, final Object sourceOld, final Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> contextOld, AMap<String, Object> contextNew, boolean isDerived) {
        logger.debug (new AFunction0NoThrow<String>() {
            @Override public String apply() {
                return "diff value: " + sourceOld + " <-> " + sourceNew + " @ " + path;
            }
        });
        try {
            valueMappings.expectedEntryFor(types, path).diff(diffBuilder, sourceOld, sourceNew, types, this, contextOld, contextNew, path, isDerived);
        } catch (Exception exc) {
            AMapperExceptionHandler.onError(exc, path);
        }
    }

    @Override public void diffObject(final APath path, final Object sourceOldRaw, final Object sourceNewRaw, AQualifiedSourceAndTargetType types, AMap<String, Object> contextOld, AMap<String, Object> contextNew, boolean isDerived) {
        logger.debug (new AFunction0NoThrow<String>() {
            @Override
            public String apply() {
                return "diff object: " + sourceOldRaw + " <-> " + sourceNewRaw + " @ " + path;
            }
        });

        try {
            final APreProcessor pre = preProcessor.tryEntryFor(types).getOrElse(APreProcessor.NO_PREPROCESSOR);
            final AOption<Object> optPreProcessedOld = pre.preProcess(sourceOldRaw, types);
            final AOption<Object> optPreProcessedNew = pre.preProcess(sourceNewRaw, types);

            if(optPreProcessedOld.isDefined() != optPreProcessedNew.isDefined()) {
                logger.diffPreProcessMismatch(path);
                return;
            }

            if(optPreProcessedOld.isEmpty()) {
                // both elements filtered out by preprocessor --> do nothing
                return;
            }

            final Object sourceOld = optPreProcessedOld.get();
            final Object sourceNew = optPreProcessedNew.get();

            doDiffObject(path, sourceOld, sourceNew, types, contextOld, contextNew, isDerived);
        } catch (Exception exc) {
            AMapperExceptionHandler.onError(exc, path);
        }
    }


    private void doDiffObject(APath path, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> contextOldOrig, AMap<String, Object> contextNewOrig, boolean isDerived) throws Exception {
        final AMap<String, Object> oldContext = contextExtractor.withContext (contextOldOrig, sourceOld, types.sourceType);
        final AMap<String, Object> newContext = contextExtractor.withContext (contextNewOrig, sourceNew, types.sourceType);

        boolean causesDerived = false;
        if(sourceOld == null && sourceNew != null) {
            diffBuilder.add (ADiffElement.added(path, isDerived, identifierExtractor.uniqueIdentifier(sourceNew, types)));
            causesDerived = true;
        }
        else if(sourceOld != null && sourceNew == null) {
            diffBuilder.add (ADiffElement.removed(path, isDerived, identifierExtractor.uniqueIdentifier(sourceOld, types)));
            causesDerived = true;
        }
        else {
            final Object oldIdent = identifierExtractor.uniqueIdentifier (sourceOld, types);
            final Object newIdent = identifierExtractor.uniqueIdentifier (sourceNew, types);
            if (! oldIdent.equals(newIdent)) {
                diffBuilder.add(ADiffElement.refChanged(path, isDerived, oldIdent, newIdent));
                causesDerived = true;
            }
        }

        objectMappings.expectedEntryFor(types, path).diff(diffBuilder, sourceOld, sourceNew, types, this, oldContext, newContext, path, isDerived || causesDerived);
        if (! identityCache.add(new IdentityPair(sourceOld, sourceNew))) {
            logger.duplicateRegistration(path, new IdentityPair(sourceOld, sourceNew));
        }
    }


    @Override public void diffDeferred(final APath path, final Object sourceOldRaw, final Object sourceNewRaw, final AQualifiedSourceAndTargetType types, final AMap<String, Object> contextOld, final AMap<String, Object> contextNew, final boolean isDerived) {
        logger.debug (new AFunction0NoThrow<String>() {
            @Override
            public String apply() {
                return "diff deferred: " + sourceOldRaw + " <-> " + sourceNewRaw + " @ " + path;
            }
        });

        deferredWork.add(new AStatement0<RuntimeException>() {
            @Override public void apply() {
                logger.debug(new AFunction0NoThrow<String>() {
                    @Override
                    public String apply() {
                        return "processing deferred diff: " + types + "@" + path;
                    }
                });

                try {
                    final APreProcessor pre = preProcessor.tryEntryFor(types).getOrElse(APreProcessor.NO_PREPROCESSOR);
                    final AOption<Object> optPreProcessedOld = pre.preProcess(sourceOldRaw, types);
                    final AOption<Object> optPreProcessedNew = pre.preProcess(sourceNewRaw, types);

                    if(optPreProcessedOld.isDefined() != optPreProcessedNew.isDefined()) {
                        logger.diffPreProcessMismatch(path);
                        return;
                    }

                    if(optPreProcessedOld.isEmpty()) {
                        // both elements filtered out by preprocessor --> do nothing
                        return;
                    }

                    final Object sourceOld = optPreProcessedOld.get();
                    final Object sourceNew = optPreProcessedNew.get();

                    if(identityCache.contains(new IdentityPair(sourceOld, sourceNew))) {
                        return;
                    }

                    logger.deferredWithoutInitial(path);
                    doDiffObject(path, sourceOld, sourceNew, types, contextOld, contextNew, isDerived);
                } catch (Exception exc) {
                    AMapperExceptionHandler.onError(exc, path);
                }
            }
        });
    }

    /**
     * two instances are equals iff the contained pair is identical.
     */
    private static class IdentityPair {
        private final Object first;
        private final Object second;

        private IdentityPair(Object first, Object second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "IdentityPair{" +
                    "first=" + first +
                    ", second=" + second +
                    '}';
        }

        @Override public boolean equals(Object o) {
            final IdentityPair other = (IdentityPair) o;
            return first == other.first && second == other.second;
        }

        @Override public int hashCode() {
            return AEquality.IDENTITY.hashCode(first) ^ AEquality.IDENTITY.hashCode(second);
        }
    }
}
