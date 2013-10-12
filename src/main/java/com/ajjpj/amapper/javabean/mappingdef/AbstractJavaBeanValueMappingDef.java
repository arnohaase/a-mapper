package com.ajjpj.amapper.javabean.mappingdef;

import com.ajjpj.amapper.core.AValueMappingDef;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.JavaBeanType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;

/**
 * @author arno
 */
public abstract class AbstractJavaBeanValueMappingDef<S,T,H> implements AValueMappingDef<S,T,H> {
    private final JavaBeanType<S> sourceType;
    private final JavaBeanType<T> targetType;

    public AbstractJavaBeanValueMappingDef(Class<S> sourceClass, Class<T> targetClass) {
        this.sourceType = JavaBeanTypes.create(sourceClass);
        this.targetType = JavaBeanTypes.create(targetClass);
    }

    @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
        return sourceType.equals(types.sourceType) && targetType.equals(types.targetType);
    }
}
