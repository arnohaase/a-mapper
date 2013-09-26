package com.ajjpj.amapper.core2;


import com.ajjpj.amapper.core2.diff.ADiffBuilder;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core2.tpe.AType;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * @author arno
 */
public class MappingDefTools {
    public static<S,T,H> AObjectMappingDef<S,T,H> forTypes(final AType sourceType, final AType targetType, final AObjectMappingDef<S,T,H> inner) {
        return new AObjectMappingDef<S, T, H>() {
            @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
                return types.sourceType.equals(sourceType) && types.targetType.equals(targetType);
            }

            @Override public boolean isCacheable() {
                return inner.isCacheable();
            }

            @Override public T map(S source, T target, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context, APath path) {
                return inner.map(source, target, types, worker, context, path);
            }

            @Override public void diff(ADiffBuilder diff, S sourceOld, S sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
                inner.diff(diff, sourceOld, sourceNew, types, worker, contextOld, contextNew, path, isDerived);
            }
        };
    }

    public static<S,T,H> AObjectMappingDef<S,T,H> requireSourceQualifier(final String sourceQualifier, final AObjectMappingDef<S,T,H> inner) {
        return new AObjectMappingDef<S, T, H>() {
            @Override
            public boolean canHandle(AQualifiedSourceAndTargetType types) {
                return inner.canHandle(types) && types.sourceQualifier.get(sourceQualifier).isDefined();
            }

            @Override public boolean isCacheable() {
                return inner.isCacheable();
            }

            @Override public T map(S source, T target, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context, APath path) {
                return inner.map(source, target, types, worker, context, path);
            }

            @Override public void diff(ADiffBuilder diff, S sourceOld, S sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
                inner.diff(diff, sourceOld, sourceNew, types, worker, contextOld, contextNew, path, isDerived);
            }
        };
    }

    public static<S,T,H> AObjectMappingDef<S,T,H> requireTargetQualifier(final String targetQualifier, final AObjectMappingDef<S,T,H> inner) {
        return new AObjectMappingDef<S, T, H>() {
            @Override
            public boolean canHandle(AQualifiedSourceAndTargetType types) {
                return inner.canHandle(types) && types.targetQualifier.get(targetQualifier).isDefined();
            }

            @Override public boolean isCacheable() {
                return inner.isCacheable();
            }

            @Override public T map(S source, T target, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context, APath path) {
                return inner.map(source, target, types, worker, context, path);
            }

            @Override public void diff(ADiffBuilder diff, S sourceOld, S sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
                inner.diff(diff, sourceOld, sourceNew, types, worker, contextOld, contextNew, path, isDerived);
            }
        };
    }

    public static<S,T,H> AValueMappingDef<S,T,H> requireSourceQualifier(final String sourceQualifier, final AValueMappingDef<S,T,H> inner) {
        return new AValueMappingDef<S, T, H>() {
            @Override
            public boolean canHandle(AQualifiedSourceAndTargetType types) {
                return inner.canHandle(types) && types.sourceQualifier.get(sourceQualifier).isDefined();
            }

            @Override
            public T map(S sourceValue, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context) {
                return inner.map(sourceValue, types, worker, context);
            }

            @Override public void diff(ADiffBuilder diff, S sourceOld, S sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
                inner.diff(diff, sourceOld, sourceNew, types, worker, contextOld, contextNew, path, isDerived);
            }
        };
    }

    public static<S,T,H> AValueMappingDef<S,T,H> requireTargetQualifier(final String targetQualifier, final AValueMappingDef<S,T,H> inner) {
        return new AValueMappingDef<S, T, H>() {
            @Override
            public boolean canHandle(AQualifiedSourceAndTargetType types) {
                return inner.canHandle(types) && types.targetQualifier.get(targetQualifier).isDefined();
            }

            @Override
            public T map(S sourceValue, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context) {
                return inner.map(sourceValue, types, worker, context);
            }

            @Override public void diff(ADiffBuilder diff, S sourceOld, S sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
                inner.diff(diff, sourceOld, sourceNew, types, worker, contextOld, contextNew, path, isDerived);
            }
        };
    }
}
