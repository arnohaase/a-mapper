package com.ajjpj.amapper.core.tpe;

/**
 * @author arno
 */
public class AQualifiedSourceAndTargetType {
    private final AType sourceType;
    private final AQualifier sourceQualifier;
    private final AType targetType;
    private final AQualifier targetQualifier;

    /**
     * convenience factory method for unqualified types
     */
    public static AQualifiedSourceAndTargetType create (AType sourceType, AType targetType) {
        return new AQualifiedSourceAndTargetType (sourceType, AQualifier.NO_QUALIFIER, targetType, AQualifier.NO_QUALIFIER);
    }

    public static AQualifiedSourceAndTargetType create (AQualifiedType source, AQualifiedType target) {
        return create (source.tpe, source.qualifier, target.tpe, target.qualifier);
    }

    public static AQualifiedSourceAndTargetType create (AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) {
        return new AQualifiedSourceAndTargetType (sourceType, sourceQualifier, targetType, targetQualifier);
    }

    private AQualifiedSourceAndTargetType(AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) {
        this.sourceType = sourceType;
        this.sourceQualifier = sourceQualifier;
        this.targetType = targetType;
        this.targetQualifier = targetQualifier;
    }

    public AType sourceType() {
        return sourceType;
    }
    public AQualifier sourceQualifier() {
        return sourceQualifier;
    }
    public AQualifiedType source() {
        return new AQualifiedType(sourceType, sourceQualifier);
    }

    public AType targetType() {
        return targetType;
    }
    public AQualifier targetQualifier() {
        return targetQualifier;
    }
    public AQualifiedType target() {
        return new AQualifiedType(targetType, targetQualifier);
    }

    @Override
    public String toString() {
        return "AQualifiedSourceAndTargetType{" +
                "sourceType=" + sourceType +
                ", sourceQualifier=" + sourceQualifier +
                ", targetType=" + targetType +
                ", targetQualifier=" + targetQualifier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AQualifiedSourceAndTargetType that = (AQualifiedSourceAndTargetType) o;

        if (sourceQualifier != null ? !sourceQualifier.equals(that.sourceQualifier) : that.sourceQualifier != null)
            return false;
        if (sourceType != null ? !sourceType.equals(that.sourceType) : that.sourceType != null) return false;
        if (targetQualifier != null ? !targetQualifier.equals(that.targetQualifier) : that.targetQualifier != null)
            return false;
        if (targetType != null ? !targetType.equals(that.targetType) : that.targetType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sourceType != null ? sourceType.hashCode() : 0;
        result = 31 * result + (sourceQualifier != null ? sourceQualifier.hashCode() : 0);
        result = 31 * result + (targetType != null ? targetType.hashCode() : 0);
        result = 31 * result + (targetQualifier != null ? targetQualifier.hashCode() : 0);
        return result;
    }
}
