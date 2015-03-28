package com.ajjpj.amapper.core;

import com.ajjpj.afoundation.collection.immutable.AMap;
import com.ajjpj.amapper.core.exclog.AMapperLogger;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;

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

    void diff(APath path, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> contextOld, AMap<String, Object> contextNew, boolean isDerived) throws Exception;
    void diffValue(APath path, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> contextOld, AMap<String, Object> contextNew, boolean isDerived);
    void diffObject(APath path, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> contextOld, AMap<String, Object> contextNew, boolean isDerived) throws Exception;

    void diffDeferred(APath path, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMap<String, Object> contextOld, AMap<String, Object> contextNew, boolean isDerived);
}