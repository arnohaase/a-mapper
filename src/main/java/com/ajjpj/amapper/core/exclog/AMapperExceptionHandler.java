package com.ajjpj.amapper.core.exclog;


import com.ajjpj.amapper.core.path.APath;

/**
 * @author arno
 */
public class AMapperExceptionHandler {
    public static <T> T onError(String msg, APath path) {
        throw new AMapperException(msg, path);
    }

    public static <T> T onError(Exception exc, APath path) {
        if(exc instanceof AMapperException) {
            throw (AMapperException) exc;
        }
        throw new AMapperException(exc, path);
    }
}
