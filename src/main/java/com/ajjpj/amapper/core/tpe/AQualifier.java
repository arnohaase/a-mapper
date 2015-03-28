package com.ajjpj.amapper.core.tpe;


import com.ajjpj.afoundation.collection.immutable.AHashMap;
import com.ajjpj.afoundation.collection.immutable.AMap;
import com.ajjpj.afoundation.collection.immutable.AOption;

/**
 * @author arno
 */
public class AQualifier {
    public static final AQualifier NO_QUALIFIER = new AQualifier(AHashMap.<String, String> empty()) {
        @Override public String toString() {
            return "NoQualifier";
        }
    };

    public final AMap<String, String> map;

    public static AQualifier create (AMap<String, String> map) {
        if(map.isEmpty()) {
            return NO_QUALIFIER;
        }
        return new AQualifier(map);
    }

    private AQualifier(AMap<String, String> map) {
        this.map = map;
    }

    public AOption<String> get(String key) {
        return map.get(key);
    }

    @Override
    public String toString() {
        return "AQualifier{" + map + "}";
    }
}
