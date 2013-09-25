package com.ajjpj.amapper.core2;

import com.ajjpj.amapper.core2.tpe.AType;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * @author arno
 */
public class NoContextExtractor implements AContextExtractor {
    public static final NoContextExtractor INSTANCE = new NoContextExtractor();

    @Override public AMap<String, Object> withContext(AMap<String, Object> context, Object o, AType tpe) {
        return context;
    }
}
