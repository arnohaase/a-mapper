package com.ajjpj.amapper.core.tpe;

import com.ajjpj.amapper.core.exclog.AMapperExceptionHandler;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.util.coll.AOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author arno
 */
public class CanHandleSourceAndTargetCache<T extends CanHandleSourceAndTarget, R> {
    private final String notFoundMessage;
    private final Collection<? extends T> all;
    private final ConcurrentHashMap<AQualifiedSourceAndTargetType, AOption<R>> resolvedSingle = new ConcurrentHashMap<AQualifiedSourceAndTargetType, AOption<R>>();
    private final ConcurrentHashMap<AQualifiedSourceAndTargetType, Iterable<R>> resolvedAll = new ConcurrentHashMap<AQualifiedSourceAndTargetType, Iterable<R>>();

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
        final AOption<R> prev = resolvedSingle.get(key);
        if(prev != null) {
            return prev;
        }

        // Multi threaded code might resolve the same combination of types multiple
        //  times concurrently, with later results overwriting earlier. This does not cause harm
        //  and allows faster access to existing cached values because there is no locking.
        final AOption<R> result = findHandler(key);
        resolvedSingle.put(key, result);
        return result;
    }

    public Iterable<R> allEntriesFor(AQualifiedSourceAndTargetType key) throws Exception {
        final Iterable<R> prev = resolvedAll.get(key);
        if(prev != null) {
            return prev;
        }

        final List<R> result = new ArrayList<R>();
        for(T candidate: all) {
            if(candidate.canHandle(key)) {
                result.add((R) candidate);
            }
        }
        resolvedAll.put(key, result);
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
