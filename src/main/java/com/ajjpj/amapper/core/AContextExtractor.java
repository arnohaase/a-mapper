package com.ajjpj.amapper.core;

import com.ajjpj.amapper.core.tpe.AType;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * @author arno
 */
public interface AContextExtractor {
    AMap<String, Object> withContext(AMap<String, Object> context, Object o, AType tpe);
}
