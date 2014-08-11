package com.ajjpj.amapper.core.impl;

import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.amapper.core.exclog.AMapperExceptionHandler;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedType;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;


/**
 * @author arno
 */
class IdentityCache {
    private final Map<IdentityKey, Object> map = new HashMap<> ();

    public void register(Object source, Object target, AQualifiedType targetType, APath path) {
        final IdentityKey key = new IdentityKey (source, targetType);

        if (map.containsKey (key)) {
            final Object prev = map.get(key);
            if(prev == target) {
                AMapperExceptionHandler.onError("duplicate registration of same target " + target + " for " + source, path);
            }
            else {
                AMapperExceptionHandler.onError("duplicate registration of different target " + target + " and " + prev + " for " + source, path);
            }
        }
        else {
            map.put (key, target);
        }
    }

    public AOption<Object> get(Object source, AQualifiedType targetType) {
        final IdentityKey key = new IdentityKey (source, targetType);

        if (map.containsKey (key)) {
            return AOption.some (map.get (key));
        }
        else {
            return AOption.none();
        }
    }

    private static class IdentityKey {
        final Object sourceObject;
        final AQualifiedType targetType;

        private IdentityKey (Object sourceObject, AQualifiedType targetType) {
            this.sourceObject = sourceObject;
            this.targetType = targetType;
        }

        @Override
        public boolean equals (Object o) {
            final IdentityKey that = (IdentityKey) o;
            return sourceObject == that.sourceObject && targetType.equals (that.targetType);
        }
        @Override
        public int hashCode () {
            int result = System.identityHashCode (sourceObject);
            result = 31 * result + targetType.hashCode ();
            return result;
        }
    }
}
