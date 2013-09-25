package com.ajjpj.amapper.core2;

import com.ajjpj.amapper.core2.diff.ADiffBuilder;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core2.tpe.CanHandleSourceAndTarget;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * A value mapping transforms data for which there is no difference between several copies as long as their attribute
 *  values are the same, e.g. strings, numbers or timestamps.

 * @author arno
 */
public interface AValueMappingDef<S,T,H> extends CanHandleSourceAndTarget {
    T map(S sourceValue, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context);
    void diff(ADiffBuilder diff, S sourceOld, S sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived);
}
