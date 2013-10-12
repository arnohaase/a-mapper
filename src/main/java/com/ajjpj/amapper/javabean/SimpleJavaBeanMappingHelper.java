package com.ajjpj.amapper.javabean;

import com.ajjpj.amapper.core.tpe.AQualifiedType;
import com.ajjpj.amapper.core.tpe.AType;

import java.util.*;

/**
 * This is a simple default implementation of the required helper methods for Java Bean mapping.
 *
 * @author arno
 */
public class SimpleJavaBeanMappingHelper implements JavaBeanMappingHelper{
    public static SimpleJavaBeanMappingHelper INSTANCE = new SimpleJavaBeanMappingHelper();

    @Override public Object createInstance(JavaBeanType<?> sourceType, JavaBeanType<?> targetType) throws Exception {
        return targetType.cls.newInstance();
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

    @Override public <T> Collection<T> createEmptyCollection(AQualifiedType tpe) throws Exception {
        if(tpe.tpe instanceof SingleParamBeanType) {
            final Class<?> collClass = ((SingleParamBeanType) tpe.tpe).cls;
            if(collClass == List.class) {
                return new ArrayList<T>();
            }
            if(collClass == Set.class) {
                return new HashSet<T>();
            }
            if(collClass == SortedSet.class) {
                return new TreeSet<T>();
            }
            return (Collection<T>) collClass.newInstance();
        }

        throw new IllegalArgumentException("unsupported collection type " + tpe);
    }
}
