package com.ajjpj.amapper.javabean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotations marked by this meta annotation are transformed into qualifiers by the default qualifier extractor.
 *
 * @author arno
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface AQualifierAnnotation {
    String name();
}
