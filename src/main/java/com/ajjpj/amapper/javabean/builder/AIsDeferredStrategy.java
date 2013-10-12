package com.ajjpj.amapper.javabean.builder;

import com.ajjpj.amapper.javabean.japi.DeferredProperty;

import java.lang.reflect.AnnotatedElement;


/**
 * @author arno
 */
public interface AIsDeferredStrategy {
    boolean isDeferred(AnnotatedElement el) throws Exception;

    AIsDeferredStrategy ANNOTATION_BASED = new AnnotationBased();

    class LiteralStrategy implements AIsDeferredStrategy {
        private final boolean isDeferred;

        public LiteralStrategy(boolean deferred) {
            isDeferred = deferred;
        }

        @Override public boolean isDeferred(AnnotatedElement el) throws Exception {
            return isDeferred;
        }
    }

    class AnnotationBased implements AIsDeferredStrategy {
        @Override public boolean isDeferred(AnnotatedElement el) throws Exception {
            return el.getAnnotation(DeferredProperty.class) != null;
        }
    }
}
