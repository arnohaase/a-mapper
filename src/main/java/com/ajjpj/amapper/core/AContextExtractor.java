package com.ajjpj.amapper.core;

import com.ajjpj.afoundation.collection.immutable.AMap;
import com.ajjpj.amapper.core.tpe.AType;


/**
 * @author arno
 */
public interface AContextExtractor {
    AMap<String, Object> withContext(AMap<String, Object> context, Object o, AType tpe);
}
