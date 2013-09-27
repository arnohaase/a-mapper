package com.ajjpj.amapper.examples.doubleToBigDecimalQualifier;


import com.ajjpj.amapper.javabean.japi.AQualifierAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author arno
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@AQualifierAnnotation(name = "Currency Rounding")
public @interface TwoDigitsRoundEven {
}
