package com.ajjpj.amapper.classes;

import com.ajjpj.amapper.javabean.annotation.AQualifierAnnotation;

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
