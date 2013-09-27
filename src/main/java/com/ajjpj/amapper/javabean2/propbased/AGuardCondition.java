package com.ajjpj.amapper.javabean2.propbased;

import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * @author arno
 */
public interface AGuardCondition<S,T,H> {
    boolean shouldMap(S source, T target, H helper, AMap<String, Object> context, APath path);
}
