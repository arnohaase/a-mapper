package com.ajjpj.amapper.javabean2.builder.qualifier;

import com.ajjpj.amapper.core2.tpe.AQualifier;
import com.ajjpj.amapper.javabean.japi.AQualifierAnnotation;
import com.ajjpj.amapper.util.coll.AHashMap;
import com.ajjpj.amapper.util.coll.AMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author arno
 */
public class AAnnotationBasedQualifierExtractor implements AQualifierExtractor {
    public static final AAnnotationBasedQualifierExtractor INSTANCE = new AAnnotationBasedQualifierExtractor();

    private static final QualAnnotDesc NO_QUAL_ANNOT = new QualAnnotDesc(false, null, null);
    private static class QualAnnotDesc {
        public final boolean isQualifier;
        public final String name;
        public final Method valueGetter;

        private QualAnnotDesc(boolean isQualifier, String name, Method valueGetter) {
            this.isQualifier = isQualifier;
            this.name = name;
            this.valueGetter = valueGetter;
        }
    }
    private final Map<Class<?>, QualAnnotDesc> cache = new ConcurrentHashMap<Class<?>, QualAnnotDesc>();

    @Override
    public AQualifier extract(AnnotatedElement el) throws Exception {
        AMap<String, String> map = AHashMap.empty();

        for(Annotation a: el.getAnnotations()) {
            final QualAnnotDesc desc = isQualifierAnnotation(a.annotationType());
            if(desc.isQualifier) {
                final Object valueRaw = desc.valueGetter != null ? desc.valueGetter.invoke(a) : null;
                final String value = valueRaw != null ? valueRaw.toString() : null;

                map = map.updated(desc.name, value);
            }
        }
        return AQualifier.create(map);
    }

    private QualAnnotDesc isQualifierAnnotation(Class<?> annotClass) {
        final QualAnnotDesc cached = cache.get(annotClass);
        if(cached != null) {
            return cached;
        }

        QualAnnotDesc result;
        final AQualifierAnnotation annot = annotClass.getAnnotation(AQualifierAnnotation.class);
        if (annot == null) {
            result = NO_QUAL_ANNOT;
        }
        else {
            final String name = annot.name();
            result = new QualAnnotDesc(true, name, valueGetter(annotClass));
        }

        cache.put(annotClass, result);
        return result;
    }

    private Method valueGetter(Class<?> annotClass) {
        try {
            return annotClass.getMethod("value");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
