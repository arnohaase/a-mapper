package com.ajjpj.amapper.javabean.propbased.accessors;

import com.ajjpj.amapper.core.compile.ACodeSnippet;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.javabean.JavaBeanType;
import com.ajjpj.amapper.util.AMapperReflectionHelper;

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
            cur = AMapperReflectionHelper.invoke (step.getGetter(), cur);
        }

        if(cur == null && isFinalStepNullSafe) {
            return null;
        }
        return AMapperReflectionHelper.invoke (finalGetter, cur);
    }

    @Override
    public void set(Object o, Object newValue) throws Exception {
        Object cur = o;
        for(Step step: steps) {
            if(cur == null && step.isNullSafe()) {
                return;
            }
            cur = AMapperReflectionHelper.invoke (step.getGetter(), cur);
        }

        if(cur == null && isFinalStepNullSafe) {
            return;
        }
        AMapperReflectionHelper.invoke (finalSetter, cur, newValue);
    }

    private boolean hasNullSafeSegment() {
        for(Step step: steps) {
            if(step.isNullSafe()) {
                return true;
            }
        }
        return isFinalStepNullSafe;
    }

    @Override
    public ACodeSnippet javaCodeForGet(ACodeSnippet parent) throws Exception {
        final StringBuilder code = new StringBuilder();

        if(hasNullSafeSegment()) {
            code.append("((" + finalGetter.getReturnType().getName() + ")(new java.util.concurrent.Callable() {\n");
            code.append("public Object call() {\n");

            String prevVarName = parent.getCode();
            for(Step step: steps) {
                if(step.isNullSafe()) {
                    code.append("if(" + prevVarName + " == null) return null;\n");
                }
                final String varName = ACodeSnippet.uniqueIdentifier();
                code.append("final " + step.getGetter().getReturnType().getName() + " " + varName + " = " + prevVarName + "." + step.getGetter().getName() + "();\n");
                prevVarName = varName;
            }

            if(isFinalStepNullSafe) {
                code.append("if(" + prevVarName + " == null) return null;\n");
            }
            code.append("return " + prevVarName + "." + finalGetter.getName() + "();\n");

            code.append("}\n");
            code.append("}).call())");
        }
        else {
            code.append(parent.getCode());
            for(Step step: steps) {
                code.append("." + step.getGetter().getName() + "()");
            }

            code.append("." + finalGetter.getName() + "()");
        }

        return new ACodeSnippet(code.toString());
    }

    @Override
    public ACodeSnippet javaCodeForSet(ACodeSnippet parent, ACodeSnippet newValue) throws Exception {
        final StringBuilder code = new StringBuilder();

        if(hasNullSafeSegment()) {
            code.append("new java.util.concurrent.Callable() {\n");
            code.append("public Object call() {\n");
            String prevVarName = parent.getCode();
            for(Step step: steps) {
                if(step.isNullSafe()) {
                    code.append("if(" + prevVarName + " == null) return null;\n");
                }
                final String varName = ACodeSnippet.uniqueIdentifier();
                code.append("final " + step.getGetter().getReturnType().getName() + " " + varName + " = " + prevVarName + "." + step.getGetter().getName() + "();\n");
                prevVarName = varName;
            }

            if(isFinalStepNullSafe) {
                code.append("if(" + prevVarName + " == null) return null;\n");
            }
            code.append(prevVarName + "." + finalSetter.getName() + "(" + newValue.getCode() + ");\n");
            code.append("return null;\n");

            code.append("}\n");
            code.append("}.call()");
        }
        else {
            code.append(parent.getCode());
            for(Step step: steps) {
                code.append("." + step.getGetter().getName() + "()");
            }

            code.append("." + finalSetter.getName() + "(" + newValue.getCode() + ")");
        }

        return new ACodeSnippet(code.toString());
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

    @Override public String toString () {
        return "AMethodPathBasedPropertyAccessor{" + name + '}';
    }
}
