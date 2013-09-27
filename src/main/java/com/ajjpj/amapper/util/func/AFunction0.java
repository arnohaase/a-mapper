package com.ajjpj.amapper.util.func;

/**
 * @author arno
 */
public interface AFunction0<T,E extends Exception> {
    T apply() throws E;
}
