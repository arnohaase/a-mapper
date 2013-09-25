package com.ajjpj.amapper.core2.tpe;

import com.ajjpj.amapper.util.coll.AHashMap;
import com.ajjpj.amapper.util.coll.AMap;
import com.ajjpj.amapper.util.coll.AOption;

/**
 * @author arno
 */
public class AQualifier {
    public static final AQualifier NO_QUALIFIER = new AQualifier(AHashMap.<String, String> empty());

    public final AMap<String, String> map;

    public AQualifier(AMap<String, String> map) {
        this.map = map;
    }

    public AOption<String> get(String key) {
        return map.get(key);
    }
}
