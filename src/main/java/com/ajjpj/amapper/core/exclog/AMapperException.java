package com.ajjpj.amapper.core.exclog;

import com.ajjpj.amapper.core.path.APath;

/**
 * @author arno
 */
public class AMapperException extends RuntimeException {
    public final APath path;

    AMapperException(String msg, APath path) {
        super(msg + "@" + path);
        this.path = path;
    }

    AMapperException(Exception inner, APath path) {
        super("AMapper exception @" + path, inner);
        this.path = path;
    }
}
