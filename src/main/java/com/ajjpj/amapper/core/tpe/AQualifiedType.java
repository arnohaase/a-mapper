package com.ajjpj.amapper.core.tpe;

/**
 * @author arno
 */
public class AQualifiedType {
    public final AType tpe;
    public final AQualifier qualifier;

    public AQualifiedType(AType tpe, AQualifier qualifier) {
        this.tpe = tpe;
        this.qualifier = qualifier;
    }

    @Override
    public String toString() {
        return "AQualifiedType{" +
                "tpe=" + tpe +
                ", qualifier=" + qualifier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AQualifiedType that = (AQualifiedType) o;

        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) return false;
        if (tpe != null ? !tpe.equals(that.tpe) : that.tpe != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tpe != null ? tpe.hashCode() : 0;
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        return result;
    }
}
