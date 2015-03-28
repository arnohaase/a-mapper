package com.ajjpj.amapper.javabean.builder;

import com.ajjpj.afoundation.collection.ACollectionHelper;
import com.ajjpj.afoundation.collection.immutable.AOption;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.javabean.JavaBeanType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.builder.qualifier.AQualifierExtractor;
import com.ajjpj.amapper.javabean.propbased.accessors.AMethodPathBasedPropertyAccessor;
import com.ajjpj.amapper.javabean.propbased.accessors.AOgnlPropertyAccessor;
import com.ajjpj.amapper.javabean.propbased.accessors.APropertyAccessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author arno
 */
public class ABeanExpressionParser {
    private final AQualifierExtractor qualifierExtractor;

    public ABeanExpressionParser(AQualifierExtractor qualifierExtractor) {
        this.qualifierExtractor = qualifierExtractor;
    }

    public APropertyAccessor parse (Class<?> parentClass, String expression, JavaBeanType<?> tpe, boolean isDeferred) throws Exception {
        final String[] segments = expression.split("\\.");

        try {
            switch(segments.length) {
                case 1:
                    final APropertyAccessor result = new JavaBeanSupport(new AIsDeferredStrategy.LiteralStrategy(isDeferred), qualifierExtractor).getBeanProperty(parentClass, expression).get();
                    if (!JavaBeanTypes.normalized (tpe.cls).equals (JavaBeanTypes.normalized (result.getType ().cls))) {
                        throw new IllegalArgumentException ("property " + expression + " of type " + parentClass.getName () + " has type " + result.getType().cls.getName () + " instead of specified type " + tpe.cls.getName());
                    }
                    return result;
                default: return asPropCascade(parentClass, expression, segments, tpe, isDeferred);
            }
        }
        catch(IllegalArgumentException exc) {
            throw exc;
        }
        catch(Exception exc) {
            return new AOgnlPropertyAccessor(expression, expression, parentClass, isDeferred, tpe, AQualifier.NO_QUALIFIER, AQualifier.NO_QUALIFIER);
        }
    }

    public APropertyAccessor parseHeuristically (Class<?> parentClass, String expression, AIsDeferredStrategy deferredStrategy) throws Exception {
        final JavaBeanSupport.AccessorDetails details = guessType (parentClass, expression, deferredStrategy);
        return parse (parentClass, expression, details.tpe, details.isDeferred);
    }

    /**
     * Uses reflection to guess the type of a given property of a class.
     * @param propCascade the name of the property. This may be a dot-separated sequence of identifiers.
     */
    public JavaBeanSupport.AccessorDetails guessType (Class<?> parentClass, String propCascade, AIsDeferredStrategy deferredStrategy) throws Exception {
        Class<?> curClass = parentClass;
        JavaBeanSupport.AccessorDetails getter = null;

        final JavaBeanSupport beanSupport = new JavaBeanSupport (deferredStrategy, qualifierExtractor);

        for(String segment: propCascade.split("\\.")) {
            if (segment.startsWith ("?")) {
                segment = segment.substring (1);
            }

            final AOption<JavaBeanSupport.AccessorDetails> optGetter = beanSupport.getGetter (curClass, segment);
            if (optGetter.isEmpty ()) {
                throw new IllegalArgumentException ("Type " + curClass + " has no property " + segment + " (as part of path " + propCascade + " on type " + parentClass.getName () + ")");
            }
            getter = optGetter.get ();
            curClass = getter.method.getReturnType ();
        }
        return getter;
    }


    private APropertyAccessor asPropCascade(Class<?> parentClass, String propName, String[] segments, JavaBeanType<?> tpe, boolean isDeferred) throws Exception {
        final List<AMethodPathBasedPropertyAccessor.Step> steps = new ArrayList<>();

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

        if (! JavaBeanTypes.normalized (finalGetter.getReturnType ()).equals (JavaBeanTypes.normalized (tpe.cls))) {
            final String path = ACollectionHelper.mkString (Arrays.asList (segments), ".");
            throw new IllegalArgumentException ("property " + path + " of type " + parentClass.getName () + " has type " + finalGetter.getReturnType ().getName () + " instead of specified type " + tpe.cls.getName ());
        }

        return new AMethodPathBasedPropertyAccessor(propName, steps, finalGetter, finalSetter, isFinalStepNullSafe, isDeferred, tpe, sourceQualifier, targetQualifier);
    }
}
