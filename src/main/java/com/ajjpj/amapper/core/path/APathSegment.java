package com.ajjpj.amapper.core.path;

import java.io.Serializable;


/**
 * @author arno
 */
public abstract class APathSegment implements Serializable {
    private final String name;

    public static ASimplePathSegment simple(String name) {
        return new ASimplePathSegment(name);
    }

    public static AParameterizedPathSegment parameterized(String name, Object key) {
        return new AParameterizedPathSegment(name, -1, key);
    }
    public static AParameterizedPathSegment parameterized(String name, int index, Object key) {
        return new AParameterizedPathSegment(name, index, key);
    }

    protected APathSegment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        APathSegment that = (APathSegment) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
