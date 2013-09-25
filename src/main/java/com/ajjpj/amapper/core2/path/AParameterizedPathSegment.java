package com.ajjpj.amapper.core2.path;

/**
 * @author arno
 */
public class AParameterizedPathSegment extends APathSegment {
    private final Object key;

    protected AParameterizedPathSegment(String name, Object key) {
        super(name);
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AParameterizedPathSegment that = (AParameterizedPathSegment) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }
}
