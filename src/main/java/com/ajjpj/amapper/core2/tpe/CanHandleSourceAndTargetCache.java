package com.ajjpj.amapper.core2.tpe;

import com.ajjpj.amapper.core2.exclog.AMapperException;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.util.coll.AOption;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author arno
 */
public class CanHandleSourceAndTargetCache<T extends CanHandleSourceAndTarget, R> {
    private final String notFoundMessage;
    private final Iterable<T> all;
    private final ConcurrentHashMap<AQualifiedSourceAndTargetType, AOption<R>> resolved = new ConcurrentHashMap<AQualifiedSourceAndTargetType, AOption<R>>();

    public CanHandleSourceAndTargetCache(String notFoundMessage, Iterable<T> all) {
        this.notFoundMessage = notFoundMessage;
        this.all = all;
    }

    public R expectedEntryFor(AQualifiedSourceAndTargetType key, APath path) {
        final AOption<R> raw = tryEntryFor(key);

        if(raw.isEmpty()) {
            throw new AMapperException(notFoundMessage + key, path);
        }
        return raw.get();
    }

    public AOption<R> tryEntryFor(AQualifiedSourceAndTargetType key) {
        final AOption<R> prev = resolved.get(key);
        if(prev != null) {
            return prev;
        }

        // Multi threaded code might resolve the same combination of types multiple
        //  times concurrently, with later results overwriting earlier. This does not cause harm
        //  and allows faster access to existing cached values because there is no locking.
        final AOption<R> result = findHandler(key);
        resolved.put(key, result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private AOption<R> findHandler(AQualifiedSourceAndTargetType key) {
        for(T candidate: all) {
            if(candidate.canHandle(key)) {
                return AOption.some ((R) candidate);
            }
        }
        return AOption.none();
    }
}
