package com.ajjpj.amapper.javabean2.mappingdef;


import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean2.JavaBeanType;

/**
 * This class requires types of source and target to be the same, but they need not be the exact type passed in - a subtype suffices.
 *
 * @author arno
 */
class SubTypeCheckingPassThroughValueMappingDef<T> extends PassThroughValueMappingDef<T> {
    SubTypeCheckingPassThroughValueMappingDef(Class<T> cls) {
        super(cls);
    }

    @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
        if (! types.sourceType.equals(types.targetType)) {
            return false;
        }

        if(! (types.sourceType instanceof JavaBeanType)) {
            return false;
        }

        return tpe.cls.isAssignableFrom (((JavaBeanType<?>) types.sourceType).cls);
    }
}
