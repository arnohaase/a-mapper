package com.ajjpj.amapper.javabean.propbased.accessors;

import com.ajjpj.amapper.core.compile.ACodeSnippet;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.javabean.JavaBeanType;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author arno
 */
public class AMethodPathBasedPropertyAccessor implements APropertyAccessor {
    private final String name;
    private final List<Step> steps;
    private final Method finalGetter;
    private final Method finalSetter;
    private final boolean isFinalStepNullSafe;
    private final boolean isDeferred;
    private final JavaBeanType<?> tpe;
    private final AQualifier sourceQualifier;
    private final AQualifier targetQualifier;

    public AMethodPathBasedPropertyAccessor(String name, List<Step> steps, Method finalGetter, Method finalSetter, boolean isFinalStepNullSafe, boolean isDeferred, JavaBeanType<?> tpe, AQualifier sourceQualifier, AQualifier targetQualifier) {
        this.name = name;
        this.steps = steps;
        this.finalGetter = finalGetter;
        this.finalSetter = finalSetter;
        this.isFinalStepNullSafe = isFinalStepNullSafe;
        this.isDeferred = isDeferred;
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
        return finalSetter != null;
    }

    @Override public Object get(Object o) throws Exception {
        Object cur = o;
        for(Step step: steps) {
            if(cur == null && step.isNullSafe()) {
                return null;
            }
            cur = step.getGetter().invoke(cur);
        }

        if(cur == null && isFinalStepNullSafe) {
            return null;
        }
        return finalGetter.invoke(cur);
    }

    @Override
    public void set(Object o, Object newValue) throws Exception {
        Object cur = o;
        for(Step step: steps) {
            if(cur == null && step.isNullSafe()) {
                return;
            }
            cur = step.getGetter().invoke(cur);
        }

        if(cur == null && isFinalStepNullSafe) {
            return;
        }
        finalSetter.invoke(cur, newValue);
    }

    @Override
    public ACodeSnippet javaCodeForGet(ACodeSnippet parent) throws Exception {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public ACodeSnippet javaCodeForSet(ACodeSnippet parent, ACodeSnippet newValue) throws Exception {
        throw new UnsupportedOperationException("TODO");
    }

    public static class Step {
        private final Method getter;
        private final boolean nullSafe;

        public Step(Method getter, boolean nullSafe) {
            this.getter = getter;
            this.nullSafe = nullSafe;
        }

        public Method getGetter() {
            return getter;
        }

        public boolean isNullSafe () {
            return nullSafe;
        }
    }
}
