package com.ajjpj.amapper.core.compile;

/**
 * This class provides a way to 'inject' data into generated code.
 *
 * @author arno
 */
public class AInjectedField {
    private final String fieldName;
    private final String fieldType;
    private final Object fieldValue;

    /**
     * @param fieldName is the variable name
     * @param fieldType is the Java type name of the field.
     * @param fieldValue is the actual value that is injected into the field. Its type must be assignable to fieldType.
     */
    public AInjectedField(String fieldName, String fieldType, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
