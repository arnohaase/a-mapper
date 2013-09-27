package com.ajjpj.amapper.javabean2.builder;

import com.ajjpj.amapper.core2.tpe.AQualifier;
import com.ajjpj.amapper.javabean2.JavaBeanType;
import com.ajjpj.amapper.javabean2.builder.qualifier.AQualifierExtractor;
import com.ajjpj.amapper.javabean2.propbased.accessors.AMethodPathBasedPropertyAccessor;
import com.ajjpj.amapper.javabean2.propbased.accessors.AOgnlPropertyAccessor;
import com.ajjpj.amapper.javabean2.propbased.accessors.APropertyAccessor;
import com.ajjpj.amapper.util.coll.AOption;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author arno
 */
public class ABeanExpressionParser {
    private final AQualifierExtractor qualifierExtractor;

    public ABeanExpressionParser(AQualifierExtractor qualifierExtractor) {
        this.qualifierExtractor = qualifierExtractor;
    }

    public APropertyAccessor parse(Class<?> parentClass, String expression, JavaBeanType<?> tpe, boolean isDeferred) throws Exception {
        final String[] segments = expression.split("\\.");

        try {
            switch(segments.length) {
                case 1: return new JavaBeanSupport(new AIsDeferredStrategy.LiteralStrategy(isDeferred), qualifierExtractor).getBeanProperty(parentClass, expression).get();
                default: return asPropCascade(parentClass, expression, segments, tpe, isDeferred);
            }
        }
        catch(Exception exc) {
            return new AOgnlPropertyAccessor(expression, expression, parentClass, isDeferred, tpe, AQualifier.NO_QUALIFIER, AQualifier.NO_QUALIFIER);
        }
    }


    private APropertyAccessor asPropCascade(Class<?> parentClass, String propName, String[] segments, JavaBeanType<?> tpe, boolean isDeferred) throws Exception {
        final List<AMethodPathBasedPropertyAccessor.Step> steps = new ArrayList<AMethodPathBasedPropertyAccessor.Step>();

        final JavaBeanSupport beanSupport = new JavaBeanSupport(new AIsDeferredStrategy.LiteralStrategy(isDeferred), qualifierExtractor);

        JavaBeanSupport.AccessorDetails lastGetterDetails = null;

        Class<?> cur = parentClass;
        for(String segRaw: segments) {
            final boolean nullSafe = segRaw.startsWith("?");
            final String seg = nullSafe ? segRaw.substring(1) : segRaw;

            lastGetterDetails = beanSupport.getGetter(cur, seg).get();
            steps.add(new AMethodPathBasedPropertyAccessor.Step(lastGetterDetails.method, nullSafe));
            cur = lastGetterDetails.method.getReturnType();
        }

        final boolean isFinalStepNullSafe = steps.remove(steps.size()-1).isNullSafe();
        final AOption<JavaBeanSupport.AccessorDetails> optFinalSetter = beanSupport.getSetterFor(lastGetterDetails.propName, lastGetterDetails.method);

        final Method finalGetter = lastGetterDetails.method;
        final Method finalSetter = optFinalSetter.isDefined() ? optFinalSetter.get().method : null;
        final AQualifier sourceQualifier = lastGetterDetails.qualifier;
        final AQualifier targetQualifier = optFinalSetter.isDefined() ? optFinalSetter.get().qualifier : AQualifier.NO_QUALIFIER;

        return new AMethodPathBasedPropertyAccessor(propName, steps, finalGetter, finalSetter, isFinalStepNullSafe, isDeferred, tpe, sourceQualifier, targetQualifier);
    }
}
