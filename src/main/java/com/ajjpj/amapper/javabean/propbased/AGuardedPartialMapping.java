package com.ajjpj.amapper.javabean.propbased;

import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;


/**
 * This class adds a condition to a partial mapping. This condition is evaluated whenever the mapping is used, and based on the conditions's
 *  value the partial mapping is applied or skipped.<p />
 *
 * This mechanism is helpful e.g. for permission based mapping.
 *
 * @author arno
 */
public class AGuardedPartialMapping<S,T,H> implements APartialBeanMapping<S,T,H> {
    private final APartialBeanMapping<S,T,H> inner;
    private final AGuardCondition<S,T,H> condition;

    public AGuardedPartialMapping(APartialBeanMapping<S, T, H> inner, AGuardCondition<S, T, H> condition) {
        this.inner = inner;
        this.condition = condition;
    }

    @Override public String getSourceName() {
        return inner.getSourceName();
    }

    @Override public String getTargetName() {
        return inner.getTargetName();
    }

    @Override public void doMap(S source, T target, AMapperWorker<? extends H> worker, AMap<String, Object> context, APath path) throws Exception {
        if(condition.shouldMap(source, target, worker.getHelpers(), context, path)) {
            inner.doMap(source, target, worker, context, path);
        }
    }

    @Override
    public void doDiff(ADiffBuilder diff, S sourceOld, S sourceNew, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
        final boolean shouldMapOld = condition.shouldMap(sourceOld, null, worker.getHelpers(), contextOld, path);
        final boolean shouldMapNew = condition.shouldMap(sourceNew, null, worker.getHelpers(), contextNew, path);

        if(shouldMapOld != shouldMapNew) {
            //TODO log a warning
        }
        else {
            if(shouldMapOld) {
                inner.doDiff(diff, sourceOld, sourceNew, worker, contextOld, contextNew, path, isDerived);
            }
        }
    }
}
