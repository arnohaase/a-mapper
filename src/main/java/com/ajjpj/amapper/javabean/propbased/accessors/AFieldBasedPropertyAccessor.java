package com.ajjpj.amapper.javabean.propbased.accessors;

import com.ajjpj.amapper.core.compile.ACodeSnippet;
import com.ajjpj.amapper.core.compile.AInjectedField;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.javabean.JavaBeanType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;


/**
 * @author arno
 */
public class AFieldBasedPropertyAccessor implements APropertyAccessor {
    private final String name;
    private final Field field;
    private final boolean isWritable;
    private final boolean isDeferred;
    private final JavaBeanType<?> tpe;
    private final AQualifier sourceQualifier;
    private final AQualifier targetQualifier;

    public AFieldBasedPropertyAccessor(String name, Field field, boolean deferred, JavaBeanType<?> tpe, AQualifier sourceQualifier, AQualifier targetQualifier) {
        if(!Modifier.isPublic (field.getModifiers ()) && !field.isAccessible()) {
            field.setAccessible(true);
        }

        this.name = name;
        this.field = field;
        isWritable = ! Modifier.isFinal(field.getModifiers());
        isDeferred = deferred;
        this.tpe = tpe;
        this.sourceQualifier = sourceQualifier;
        this.targetQualifier = targetQualifier;
    }

    @Override public String getName() {
        return name;
    }

    @Override public JavaBeanType<?> getType() {
        return tpe;
    }

    @Override public AQualifier getSourceQualifier() {
        return sourceQualifier;
    }

    @Override public AQualifier getTargetQualifier() {
        return targetQualifier;
    }

    @Override public boolean isDeferred() {
        return isDeferred;
    }

    @Override public boolean isWritable() {
        return isWritable;
    }

    @Override public Object get(Object o) throws Exception {
        return field.get(o);
    }

    @Override public void set(Object o, Object newValue) throws Exception {
        field.set(o, newValue);
    }

    @Override public ACodeSnippet javaCodeForGet(ACodeSnippet parent) throws Exception {
        final String fieldName = ACodeSnippet.uniqueIdentifier();
        return new ACodeSnippet(fieldName + ".get(" + parent.getCode() + ")", Arrays.asList(new AInjectedField(fieldName, Field.class.getName(), field)));
    }

    @Override public ACodeSnippet javaCodeForSet(ACodeSnippet parent, ACodeSnippet newValue) throws Exception {
        final String fieldName = ACodeSnippet.uniqueIdentifier();
        return new ACodeSnippet(fieldName + ".set(" + parent.getCode() + ", " + newValue.getCode() + ")", Arrays.asList(new AInjectedField(fieldName, Field.class.getName(), field)));
    }
}
