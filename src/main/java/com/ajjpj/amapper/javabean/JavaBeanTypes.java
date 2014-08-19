package com.ajjpj.amapper.javabean;

import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.amapper.core.tpe.AType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * @author arno
 */
public class JavaBeanTypes {
    private static final Map<Class<?>, Class<?>> boxedEquivalents = new HashMap<Class<?>, Class<?>>();
    static {
        boxedEquivalents.put(Boolean.  TYPE, Boolean.class);
        boxedEquivalents.put(Character.TYPE, Character.class);
        boxedEquivalents.put(Byte.     TYPE, Byte.class);
        boxedEquivalents.put(Short.    TYPE, Short.class);
        boxedEquivalents.put(Integer.  TYPE, Integer.class);
        boxedEquivalents.put(Long.     TYPE, Long.class);
        boxedEquivalents.put(Float.    TYPE, Float.class);
        boxedEquivalents.put(Double.   TYPE, Double.class);
    }

    public static <T> JavaBeanType<T> create(Class<T> cls) {
        if (cls.isArray ()) {
            return create (cls, cls.getComponentType ());
        }

        return new JavaBeanType<T>(cls);
    }
    public static <T,P> SingleParamBeanType<T,P> create (Class<T> cls, Class<P> param) {
        return new SingleParamBeanType<>(cls, param);
    }
    public static AOption<? extends JavaBeanType> create(Type javaType) {
        if (javaType instanceof Class<?>) {
            return AOption.some (create ((Class) javaType));
        }
        if (! (javaType instanceof ParameterizedType)) {
            return AOption.none ();
        }

        final ParameterizedType pt = (ParameterizedType) javaType;
        if(pt.getActualTypeArguments().length == 1) {
            final AOption<Class> paramType = rawType (pt.getActualTypeArguments()[0]);
            if (paramType.isEmpty ()) {
                return AOption.some (create (rawType(pt).get()));
            }
            return AOption.some (create (rawType(pt).get (), paramType.get ()));
        }
        return AOption.some (create(rawType(pt).get()));
    }

    public static boolean isSubtypeOrSameOf(AType tpe, Class<?> cls) {
        return tpe instanceof JavaBeanType && cls.isAssignableFrom(((JavaBeanType) tpe).cls);
    }

    public static boolean isArrayType (AType tpe) {
        return tpe instanceof JavaBeanType && ((JavaBeanType)tpe).cls.isArray ();
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> normalized (Class<T> cls) {
        final Class<?> boxed = boxedEquivalents.get(cls);
        return boxed != null ? (Class<T>) boxed : cls;
    }

    public static AOption<Class> rawType(Type javaType) {
        if (javaType instanceof Class<?>) {
            return AOption.some((Class) javaType);
        }
        if (javaType instanceof ParameterizedType) {
            return rawType(((ParameterizedType) javaType).getRawType());
        }

        return AOption.none ();
    }
}
