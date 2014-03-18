package com.ajjpj.amapper.javabean.propbased;

import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.amapper.core.path.APath;

/**
 * @author arno
 */
public interface AGuardCondition<S,T,H> {
    boolean shouldMap(S source, T target, H helper, AMap<String, Object> context, APath path);
}
