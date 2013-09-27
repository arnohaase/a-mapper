package com.ajjpj.amapper.util.func;

/**
 * @author arno
 */
public interface AVoidFunction1<T,E extends Exception> {
    void apply(T o) throws E;
}
