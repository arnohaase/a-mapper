package com.ajjpj.amapper.core2;

import com.ajjpj.amapper.core2.diff.ADiffBuilder;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core2.tpe.CanHandleSourceAndTarget;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * Object mappings deal with data that is sensitive to object identity, particluarly objects with mutable data.

 * @author arno
 */
public interface AObjectMappingDef <S, T, H> extends CanHandleSourceAndTarget {
    /**
     * @return true iff both source and target side have object identity, i.e. calls with the <em>same</em> object
     *         must return the <em>same</em> result. That is almost always desirable - it is one of the key characteristics
     *         of object mappings as opposed to value mappings.<p />
     *
     *         The only context where a value of 'false' makes sense is if a value (e.g. a string) is mapped into an existing
     *         data structure with object identity (e.g. a map).<p />
     *
     *         If in doubt, return true.
     */
    boolean isCacheable();

    T map(S source, T target, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context, APath path) throws Exception;

    void diff(ADiffBuilder diff, S sourceOld, S sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception;
}


