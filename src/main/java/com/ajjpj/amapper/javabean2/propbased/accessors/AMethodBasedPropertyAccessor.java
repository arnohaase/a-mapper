package com.ajjpj.amapper.javabean2.propbased.accessors;

import com.ajjpj.amapper.core2.tpe.AQualifier;
import com.ajjpj.amapper.javabean2.JavaBeanType;

import java.lang.reflect.Method;

/**
 * @author arno
 */
public class AMethodBasedPropertyAccessor implements APropertyAccessor {
    private final String name;
    private final Method getter;
    private final Method setter;
    private final boolean isDeferred;
    private final JavaBeanType tpe;
    private final AQualifier sourceQualifier;
    private final AQualifier targetQualifier;

    public AMethodBasedPropertyAccessor(String name, Method getter, Method setter, boolean deferred, JavaBeanType tpe, AQualifier sourceQualifier, AQualifier targetQualifier) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
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
        return setter != null;
    }

    @Override public Object get(Object o) throws Exception {
        return getter.invoke(o);
    }

    @Override public void set(Object o, Object newValue) throws Exception {
        setter.invoke(o, newValue);
    }
}
