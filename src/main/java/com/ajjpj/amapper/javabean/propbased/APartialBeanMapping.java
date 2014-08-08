package com.ajjpj.amapper.javabean.propbased;

import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;

/**
 * Java Bean mappings are typically done by property, i.e. they are split into parts. This interface represents such a
 *  part, e.g. the mapping of a single property.<p>
 *
 * The type parameters represent the bean types that this mapping is part of.
 *
 * @author arno
 */
public interface APartialBeanMapping <S,T, H> {
    String getSourceName();
    String getTargetName();

    void doMap(S source, T target, AMapperWorker<? extends H> worker, AMap<String, Object> context, APath path) throws Exception;
    void doDiff(ADiffBuilder diff, S sourceOld, S sourceNew, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception;
}
