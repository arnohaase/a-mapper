package com.ajjpj.amapper.util.func;

/**
 * @author arno
 */
public interface AFunction1<R, P, E extends Exception> {
    R apply (P param) throws E;
}
