package com.ajjpj.amapper.core.tpe;

/**
 * @author arno
 */
public interface CanHandleSourceAndTarget {
    boolean canHandle(AQualifiedSourceAndTargetType types) throws Exception;
}
