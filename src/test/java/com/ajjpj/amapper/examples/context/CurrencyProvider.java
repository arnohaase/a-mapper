package com.ajjpj.amapper.examples.context;


import com.ajjpj.amapper.javabean.annotation.AContextMarker;

import java.util.Currency;

/**
 * @author arno
 */
// This marker annotation causes all objects implementing this interface to be
//  registered as context
@AContextMarker
public interface CurrencyProvider {
    Currency getCurrency();
}
