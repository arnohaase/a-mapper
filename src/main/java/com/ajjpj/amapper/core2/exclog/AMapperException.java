package com.ajjpj.amapper.core2.exclog;

import com.ajjpj.amapper.core2.path.APath;

/**
 * @author arno
 */
public class AMapperException extends RuntimeException {
    public final APath path;

    public AMapperException(String msg, APath path) {
        super(msg + "@" + path);
        this.path = path;
    }
}
