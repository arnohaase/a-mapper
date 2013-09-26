package com.ajjpj.amapper.core2;

import com.ajjpj.amapper.core2.exclog.AMapperLogger;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * A 'worker' is created per mapping execution. It has mutable state and is <em>not</em> thread safe.
 *
 * @author arno
 */
public interface AMapperDiffWorker<H> {
    AMapperLogger getLogger();

    /**
     * The mapper worker passes the 'helpers' object through to the mapping defs. 'Helpers' can have mutable state, so
     *  their lifecycle is tied to the worker' lifecycle rather than having to be global.
     */
    H getHelpers();

    AIdentifierExtractor getIdentifierExtractor();

    void diff(APath path, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> oldContext, AMap<String, Object> newContext, boolean isDerived);
    void diffValue(APath path, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> oldContext, AMap<String, Object> newContext, boolean isDerived);
    void diffObject(APath path, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> oldContext, AMap<String, Object> newContext, boolean isDerived);

    void diffDeferred(APath path, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> oldContext, AMap<String, Object> newContext, boolean isDerived);
}