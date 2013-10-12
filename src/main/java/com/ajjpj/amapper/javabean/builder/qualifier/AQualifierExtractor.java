package com.ajjpj.amapper.javabean.builder.qualifier;


import com.ajjpj.amapper.core.tpe.AQualifier;

import java.lang.reflect.AnnotatedElement;

/**
 * @author arno
 */
public interface AQualifierExtractor {
    AQualifier extract (AnnotatedElement el) throws Exception;
}
