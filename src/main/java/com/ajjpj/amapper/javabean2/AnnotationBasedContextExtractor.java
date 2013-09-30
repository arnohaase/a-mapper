package com.ajjpj.amapper.javabean2;

import com.ajjpj.amapper.core2.AContextExtractor;
import com.ajjpj.amapper.core2.tpe.AType;
import com.ajjpj.amapper.javabean.japi.AContextMarker;
import com.ajjpj.amapper.util.coll.AList;
import com.ajjpj.amapper.util.coll.AMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author arno
 */
public class AnnotationBasedContextExtractor implements AContextExtractor {
    private static final Map<AType, AList<String>> cache = new ConcurrentHashMap<AType, AList<String>>();

    @Override public AMap<String, Object> withContext(AMap<String, Object> context, Object o, AType tpe) {
        final AList<String> cacheValue = cache.get(tpe);
        if(cacheValue != null) {
            return withKeys(context, o, cacheValue);
        }

        final AList<String> keys = extractKeys(tpe);
        cache.put(tpe, keys);
        return withKeys(context, o, keys);
    }

    private AMap<String, Object> withKeys(AMap<String, Object> context, Object o, AList<String> keys) {
        AMap<String, Object> result = context;
        for(String key: keys) {
            result = result.updated(key, o);
        }
        return result;
    }

    private AList<String> extractKeys(AType tpe) {
        AList<String> result = AList.nil();

        if(tpe instanceof JavaBeanType<?>) {
            //TODO recursive interface analysis; move this to JavaBeanSupport
            for(Class<?> iface: ((JavaBeanType<?>) tpe).cls.getInterfaces()) {
                if(iface.getAnnotation(AContextMarker.class) != null) {
                    result = result.cons(iface.getName());
                }
            }
        }

        return result;
    }
}
