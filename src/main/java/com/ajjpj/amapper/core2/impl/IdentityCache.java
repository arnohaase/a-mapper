package com.ajjpj.amapper.core2.impl;

import com.ajjpj.amapper.core2.exclog.AMapperException;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.util.coll.AOption;

import java.util.IdentityHashMap;


/**
 * @author arno
 */
class IdentityCache {
    private final IdentityHashMap<Object, AOption<Object>> map = new IdentityHashMap<Object, AOption<Object>>();

    public void register(Object source, Object target, APath path) {
        if(map.containsKey(source)) {
            final Object prev = map.get(source);
            if(prev == target) {
                throw new AMapperException("duplicate registration of same target " + target + " for " + source, path);
            }
            else {
                throw new  AMapperException("duplicate registration of different target " + target + " and " + prev + " for " + source, path);
            }
        }
        else {
            map.put(source, AOption.some(target));
        }
    }

    public AOption<Object> get(Object source) {
        final AOption<Object> result = map.get(source);
        if(result != null) {
            return result;
        }
        else {
            return AOption.none();
        }
    }
}
