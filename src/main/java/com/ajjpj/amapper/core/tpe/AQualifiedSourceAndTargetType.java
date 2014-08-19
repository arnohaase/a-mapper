package com.ajjpj.amapper.core.tpe;

/**
 * @author arno
 */
public class AQualifiedSourceAndTargetType {
    private final AQualifiedType source;
    private final AQualifiedType target;

    /**
     * convenience factory method for unqualified types
     */
    public static AQualifiedSourceAndTargetType create (AType sourceType, AType targetType) {
        return create (sourceType, AQualifier.NO_QUALIFIER, targetType, AQualifier.NO_QUALIFIER);
    }

    public static AQualifiedSourceAndTargetType create (AQualifiedType source, AQualifiedType target) {
        return new AQualifiedSourceAndTargetType (source, target);
    }

    public static AQualifiedSourceAndTargetType create (AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) {
        return create (new AQualifiedType (sourceType, sourceQualifier), new AQualifiedType (targetType, targetQualifier));
    }

    private AQualifiedSourceAndTargetType(AQualifiedType source, AQualifiedType target) {
        this.source = source;
        this.target = target;
    }

    public AType sourceType() {
        return source.tpe;
    }
    public AQualifier sourceQualifier() {
        return source.qualifier;
    }
    public AQualifiedType source() {
        return source;
    }

    public AType targetType() {
        return target.tpe;
    }
    public AQualifier targetQualifier() {
        return target.qualifier;
    }
    public AQualifiedType target() {
        return target;
    }

    @Override
    public String toString() {
        return "AQualifiedSourceAndTargetType{" +
                "sourceType=" + sourceType() +
                ", sourceQualifier=" + sourceQualifier() +
                ", targetType=" + targetType() +
                ", targetQualifier=" + targetQualifier() +
                '}';
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;

        AQualifiedSourceAndTargetType that = (AQualifiedSourceAndTargetType) o;

        if (!source.equals (that.source)) return false;
        if (!target.equals (that.target)) return false;

        return true;
    }
    @Override
    public int hashCode () {
        int result = source.hashCode ();
        result = 31 * result + target.hashCode ();
        return result;
    }
}
