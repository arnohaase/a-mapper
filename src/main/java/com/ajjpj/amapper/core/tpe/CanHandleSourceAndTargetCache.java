package com.ajjpj.amapper.core.tpe;

import com.ajjpj.amapper.core.exclog.AMapperExceptionHandler;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.util.coll.AOption;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author arno
 */
public class CanHandleSourceAndTargetCache<T extends CanHandleSourceAndTarget, R> {
    private final String notFoundMessage;
    private final Collection<? extends T> all;
    private final ConcurrentHashMap<AQualifiedSourceAndTargetType, AOption<R>> resolved = new ConcurrentHashMap<AQualifiedSourceAndTargetType, AOption<R>>();

    public CanHandleSourceAndTargetCache(String notFoundMessage, Collection<? extends T> all) {
        this.notFoundMessage = notFoundMessage;
        this.all = all;
    }

    public R expectedEntryFor(AQualifiedSourceAndTargetType key, APath path) throws Exception {
        final AOption<R> raw = tryEntryFor(key);

        if(raw.isEmpty()) {
            AMapperExceptionHandler.onError(notFoundMessage + key, path);
        }
        return raw.get();
    }

    public AOption<R> tryEntryFor(AQualifiedSourceAndTargetType key) throws Exception {
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
    private AOption<R> findHandler(AQualifiedSourceAndTargetType key) throws Exception {
        for(T candidate: all) {
            if(candidate.canHandle(key)) {
                return AOption.some ((R) candidate);
            }
        }
        return AOption.none();
    }

    public Collection<? extends T> getAll() {
        return all;
    }
}
