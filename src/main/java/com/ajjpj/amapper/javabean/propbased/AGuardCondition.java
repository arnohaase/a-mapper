package com.ajjpj.amapper.javabean.propbased;

import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * @author arno
 */
public interface AGuardCondition<S,T,H> {
    boolean shouldMap(S source, T target, H helper, AMap<String, Object> context, APath path);
}
