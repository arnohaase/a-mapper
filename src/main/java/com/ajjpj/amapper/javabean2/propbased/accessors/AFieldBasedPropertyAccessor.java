package com.ajjpj.amapper.javabean2.propbased.accessors;

import com.ajjpj.amapper.core2.tpe.AQualifier;
import com.ajjpj.amapper.javabean2.JavaBeanType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


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
}
