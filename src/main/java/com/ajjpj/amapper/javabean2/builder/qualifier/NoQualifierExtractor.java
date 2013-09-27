package com.ajjpj.amapper.javabean2.builder.qualifier;

import com.ajjpj.amapper.core2.tpe.AQualifier;

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
