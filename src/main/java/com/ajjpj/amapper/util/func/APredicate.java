package com.ajjpj.amapper.util.func;

/**
 * @author arno
 */
public interface APredicate<T,E extends Exception> {
    boolean apply(T o) throws E;
}
