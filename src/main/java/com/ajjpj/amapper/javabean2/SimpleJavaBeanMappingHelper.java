package com.ajjpj.amapper.javabean2;

import com.ajjpj.amapper.core2.tpe.AQualifiedType;
import com.ajjpj.amapper.core2.tpe.AType;

import java.util.Collection;

/**
 * This is a simple default implementation of the required helper methods for Java Bean mapping.
 *
 * @author arno
 */
public class SimpleJavaBeanMappingHelper implements JavaBeanMappingHelper{
    public static SimpleJavaBeanMappingHelper INSTANCE = new SimpleJavaBeanMappingHelper();

    @Override public Object createInstance(JavaBeanType<?> tpe, JavaBeanType<?> forSourceType) throws Exception {
        return tpe.cls.newInstance();
    }

    @Override public AType elementType(AType tpe) {
        return ((SingleParamBeanType<?,?>) tpe).getParamType();
    }

    @SuppressWarnings("unchecked")
    @Override public <T> Collection<T> asJuCollection(Object coll, AQualifiedType tpe) {
        return (Collection<T>) coll;
    }

    @Override public Object fromJuCollection(Collection<?> coll, AQualifiedType tpe) {
        return coll;
    }
}
