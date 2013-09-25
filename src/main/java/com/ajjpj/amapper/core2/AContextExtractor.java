package com.ajjpj.amapper.core2;

import com.ajjpj.amapper.core2.tpe.AType;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * @author arno
 */
public interface AContextExtractor {
    AMap<String, Object> withContext(AMap<String, Object> context, Object o, AType tpe);
}
