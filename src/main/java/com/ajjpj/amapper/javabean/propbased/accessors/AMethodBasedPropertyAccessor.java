package com.ajjpj.amapper.javabean.propbased.accessors;

import com.ajjpj.amapper.core.compile.ACodeSnippet;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.javabean.JavaBeanType;
import com.ajjpj.amapper.util.AMapperReflectionHelper;

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
        return AMapperReflectionHelper.invoke (getter, o);
    }

    @Override public void set(Object o, Object newValue) throws Exception {
        AMapperReflectionHelper.invoke (setter, o, newValue);
    }

    @Override public ACodeSnippet javaCodeForGet(ACodeSnippet parent) throws Exception {
        return new ACodeSnippet(parent.getCode() + "." + getter.getName() + "()");
    }

    @Override public ACodeSnippet javaCodeForSet(ACodeSnippet parent, ACodeSnippet newValue) throws Exception {
        return new ACodeSnippet(parent.getCode() + "." + setter.getName() + "(" + newValue.getCode() + ")");
    }

    @Override
    public String toString() {
        return "MtdProp{" + name + ": " + isWritable() + "}";
    }
}
