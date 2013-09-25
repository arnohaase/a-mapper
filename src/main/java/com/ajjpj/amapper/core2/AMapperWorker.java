package com.ajjpj.amapper.core2;

import com.ajjpj.amapper.core2.exclog.AMapperLogger;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.util.coll.AMap;
import com.ajjpj.amapper.util.coll.AOption;
import com.ajjpj.amapper.util.func.AFunction0;
import com.ajjpj.amapper.util.func.AVoidFunction1;

/**
 * A 'worker' is created per mapping execution. It has mutable state and is <em>not</em> thread safe.
 *
 * @author arno
 */
public interface AMapperWorker<H> {
    AMapperLogger getLogger();

    /**
     * The mapper worker passes the 'helpers' object through to the mapping defs. 'Helpers' can have mutable state, so
     *  their lifecycle is tied to the worker' lifecycle rather than having to be global.
     */
    H getHelpers();

    AIdentifierExtractor getIdentifierExtractor();

    AOption<Object> map      (APath path, Object source, Object target, AQualifiedSourceAndTargetType types, AMap<String, Object> context);
    AOption<Object> mapObject(APath path, Object source, Object target, AQualifiedSourceAndTargetType types, AMap<String, Object> context);
    Object          mapValue (APath path, Object source,                AQualifiedSourceAndTargetType types, AMap<String, Object> context);

    /**
     * deferred mapping causes this mapping to be deferred until no non-deferred mapping work is left. The underlying
     *  abstraction is that there is a primary, hierarchical structure through which all objects can be
     *  reached, and a secondary structure of non-containment references. <p />
     * The former is mapped first so as to provide well-defined, 'normalized' paths to every element. <p />
     * All objects must be reachable through the primary hierarchy. Put differently, it is invalid for
     *  an unmapped object to be reached through a deferred 'map' call. <p />
     * NB: deferred mapping automatically means mutable state in the target object structure because the result of the
     *  mapping is created after the initial object structure is complete. This also prevents streaming processing.
     *
     */
    void mapDeferred(APath path, Object source, AFunction0<Object> target, AQualifiedSourceAndTargetType types, AVoidFunction1<Object> callback);
}