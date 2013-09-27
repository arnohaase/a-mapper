package com.ajjpj.amapper.javabean.japi.classes;

import com.ajjpj.amapper.javabean.japi.AQualifierAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author arno
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AQualifierAnnotation(name = "qualifier-test")
public @interface QualifierTestAnnotationWithValue {
    String value();
}
