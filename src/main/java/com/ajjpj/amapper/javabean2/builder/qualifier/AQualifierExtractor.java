package com.ajjpj.amapper.javabean2.builder.qualifier;


import com.ajjpj.amapper.core2.tpe.AQualifier;

import java.lang.reflect.AnnotatedElement;

/**
 * @author arno
 */
public interface AQualifierExtractor {
    AQualifier extract (AnnotatedElement el) throws Exception;
}
