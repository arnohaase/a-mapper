package com.ajjpj.amapper.javabean;

import com.ajjpj.amapper.core.tpe.AQualifiedType;

import java.util.*;

/**
 * This is a simple default implementation of the required helper methods for Java Bean mapping.
 *
 * @author arno
 */
public class SimpleJavaBeanMappingHelper implements JavaBeanMappingHelper {
    public static SimpleJavaBeanMappingHelper INSTANCE = new SimpleJavaBeanMappingHelper();

    @Override public Object provideInstance(Object source, Object targetRaw, JavaBeanType<?> sourceType, JavaBeanType<?> targetType) throws Exception {
        return targetRaw != null ? targetRaw : targetType.cls.newInstance();
    }

    @Override public AQualifiedType elementType(AQualifiedType tpe) {
        return new AQualifiedType (((SingleParamBeanType<?,?>) tpe.tpe).getParamType(), tpe.qualifier);
    }

    @SuppressWarnings("unchecked")
    @Override public <T> Collection<T> asJuCollection(Object coll, AQualifiedType tpe) {
        if (coll == null) {
            return null;
        }

        if (coll.getClass ().isArray ()) {
            final Class<?> componentType = (coll.getClass ().getComponentType ());
            if (! componentType.isPrimitive ())  return (Collection<T>) Arrays.asList ((Object[]) coll);
            if (componentType == Boolean.TYPE)   return (Collection<T>) Arrays.asList ((boolean[]) coll);
            if (componentType == Integer.TYPE)   return (Collection<T>) Arrays.asList ((int[]) coll);
            if (componentType == Long.TYPE)      return (Collection<T>) Arrays.asList ((long[]) coll);
            if (componentType == Double.TYPE)    return (Collection<T>) Arrays.asList ((double[]) coll);
            if (componentType == Byte.TYPE)      return (Collection<T>) Arrays.asList ((byte[]) coll);
            if (componentType == Short.TYPE)     return (Collection<T>) Arrays.asList ((short[]) coll);
            if (componentType == Float.TYPE)     return (Collection<T>) Arrays.asList ((float[]) coll);
            if (componentType == Character.TYPE) return (Collection<T>) Arrays.asList ((char[]) coll);
        }

        return (Collection<T>) coll;
    }

    @Override public Object fromJuCollection(Collection<?> coll, AQualifiedType tpe) {
        return coll;
    }

    @SuppressWarnings("unchecked")
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
