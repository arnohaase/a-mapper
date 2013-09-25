package com.ajjpj.amapper.core2.tpe;

/**
 * @author arno
 */
public class AQualifiedSourceAndTargetType {
    public final AType sourceType;
    public final AQualifier sourceQualifier;
    public final AType targetType;
    public final AQualifier targetQualifier;

    public AQualifiedSourceAndTargetType(AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) {
        this.sourceType = sourceType;
        this.sourceQualifier = sourceQualifier;
        this.targetType = targetType;
        this.targetQualifier = targetQualifier;
    }

    public AQualifiedType source() {
        return new AQualifiedType(sourceType, sourceQualifier);
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
