package com.ajjpj.amapper.javabean.propbased.accessors;

import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.javabean.JavaBeanType;

/**
 * @author arno
 */
public interface APropertyAccessor {
    /**
     * for debugging / logging
     */
    String getName();

    JavaBeanType<?> getType();

    AQualifier getSourceQualifier();
    AQualifier getTargetQualifier();

    boolean isDeferred();
    boolean isWritable();

    Object get(Object o) throws Exception;
    void set(Object o, Object newValue) throws Exception;
}
