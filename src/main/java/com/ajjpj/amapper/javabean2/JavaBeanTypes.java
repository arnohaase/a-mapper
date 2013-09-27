package com.ajjpj.amapper.javabean2;

import com.ajjpj.amapper.core2.tpe.AType;
import com.ajjpj.amapper.util.coll.AOption;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
        return new JavaBeanType<T>(cls);
    }
    public static <T,P> SingleParamBeanType<T,P> create (Class<T> cls, Class<P> param) {
        return new SingleParamBeanType<T,P>(cls, param);
    }
    public static JavaBeanType<?> create(Type javaType) {
        if(javaType instanceof Class<?>) {
            return create ((Class<?>) javaType);
        }

        final ParameterizedType pt = (ParameterizedType) javaType;
        if(pt.getActualTypeArguments().length == 1) {
            return create(rawType(pt), rawType(pt.getActualTypeArguments()[0]));
        }
        return create(rawType(pt));
    }

    public static boolean isSubtypeOrSameOf(AType tpe, Class<?> cls) {
        return tpe instanceof JavaBeanType && cls.isAssignableFrom(((JavaBeanType) tpe).cls);
    }

    public static JavaBeanType<?> singleCommonType(AOption<Type>... javaTypes) {
        final Set<JavaBeanType> set = new HashSet<JavaBeanType>();

        for(AOption<Type> tpe: javaTypes) {
            if(tpe.isDefined()) {
                set.add (create (tpe.get ()));
            }
        }

        switch(set.size()) {
            case 0: throw new IllegalArgumentException("no type");
            case 1: return set.iterator().next();
            default: throw new IllegalArgumentException("non-unique type: " + set);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> normalized (Class<T> cls) {
        final Class<?> boxed = boxedEquivalents.get(cls);
        return boxed != null ? (Class<T>) boxed : cls;
    }

    public static Class<?> rawType(Type javaType) {
        if(javaType instanceof Class<?>) {
            return (Class<?>) javaType;
        }

        return rawType(((ParameterizedType) javaType).getRawType());
    }
}
