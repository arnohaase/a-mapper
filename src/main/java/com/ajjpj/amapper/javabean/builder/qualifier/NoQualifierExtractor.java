package com.ajjpj.amapper.javabean.builder.qualifier;

import com.ajjpj.amapper.core.tpe.AQualifier;

import java.lang.reflect.AnnotatedElement;

/**
 * @author arno
 */
public class NoQualifierExtractor implements AQualifierExtractor {
    public static AQualifierExtractor INSTANCE = new NoQualifierExtractor();

    @Override public AQualifier extract(AnnotatedElement el) {
        return AQualifier.NO_QUALIFIER;
    }
}
