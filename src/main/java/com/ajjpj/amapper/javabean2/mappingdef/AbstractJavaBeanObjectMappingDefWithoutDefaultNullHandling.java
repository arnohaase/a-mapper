package com.ajjpj.amapper.javabean2.mappingdef;

import com.ajjpj.amapper.core2.AObjectMappingDef;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean2.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean2.JavaBeanType;
import com.ajjpj.amapper.javabean2.JavaBeanTypes;

/**
 * This class provides 'canHandle' and generics convenience for Java Bean mapping defs, leaving the handling of
 *  null values (especially on the target side) to implementations.<p />
 *
 * If you do not know what that means or if you are unsure if it is what you want, use AbstractJavaBeanMappingDef
 *  instead.
 *
 * @author arno
 */
public abstract class AbstractJavaBeanObjectMappingDefWithoutDefaultNullHandling<S,T,H extends JavaBeanMappingHelper> implements AObjectMappingDef<S,T,H> {
    public final JavaBeanType<S> sourceType;
    public final JavaBeanType<T> targetType;

    public AbstractJavaBeanObjectMappingDefWithoutDefaultNullHandling(Class<S> sourceClass, Class<T> targetClass) {
        sourceType = JavaBeanTypes.create(sourceClass);
        targetType = JavaBeanTypes.create(targetClass);
    }

    @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
        return sourceType.equals(types.sourceType) && targetType.equals(types.targetType);
    }
}
