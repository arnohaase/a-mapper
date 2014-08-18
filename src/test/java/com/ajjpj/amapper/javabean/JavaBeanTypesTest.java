package com.ajjpj.amapper.javabean;

import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.amapper.classes.ForTypeTests;
import org.junit.Test;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * @author arno
 */
public class JavaBeanTypesTest {
    @Test
    public void testFactories() {
        assertEquals(String.class, JavaBeanTypes.create(String.class).cls);
        assertEquals(Integer.class, JavaBeanTypes.create(Integer.class).cls);

        assertEquals(List.class, JavaBeanTypes.create(List.class, String.class).cls);
        assertEquals(String.class, JavaBeanTypes.create(List.class, String.class).paramCls);
        assertEquals(Set.class, JavaBeanTypes.create(Set.class, String.class).cls);
        assertEquals(String.class, JavaBeanTypes.create(Set.class, String.class).paramCls);
    }

    @SuppressWarnings ("InstantiatingObjectToGetClassObject") @Test
    public void testArray() throws Exception {
        assertEquals (new SingleParamBeanType<> (Array.class, String.class), JavaBeanTypes.create (new String[0].getClass ()));
        assertEquals (new SingleParamBeanType<> (Array.class, boolean.class), JavaBeanTypes.create (new boolean[0].getClass ()));

        assertEquals (new SingleParamBeanType<> (Array.class, String.class), JavaBeanTypes.create ((Type) new String[0].getClass ()).get());
        assertEquals (new SingleParamBeanType<> (Array.class, boolean.class), JavaBeanTypes.create ((Type) new boolean[0].getClass ()).get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testJavaType() throws Exception {
        final Type tpeString     = ForTypeTests.class.getMethod("withString").getGenericReturnType();
        final Type tpeStringList = ForTypeTests.class.getMethod("withStringList").getGenericReturnType();
        final Type tpeMap        = ForTypeTests.class.getMethod("withMap").getGenericReturnType();
        final Type tpeWildcard   = ForTypeTests.class.getMethod("withWildcardList").getGenericReturnType ();
        final Type tpeGeneric    = ForTypeTests.class.getMethod("withGeneric").getGenericReturnType ();

        assertEquals(new JavaBeanType(String.class), JavaBeanTypes.create(tpeString).get());
        assertEquals(new SingleParamBeanType(List.class, String.class), JavaBeanTypes.create(tpeStringList).get());

        assertEquals(AOption.some (String.class), JavaBeanTypes.rawType(tpeString));
        assertEquals(AOption.some (List.class),   JavaBeanTypes.rawType (tpeStringList));

        // There is no *built-in* special handling of Java types with more than one parameter, especially in generic factories. Using code
        //  is however free to provide their own implementation of JavaBeanType that provide such support
        assertEquals(new JavaBeanType(Map.class), JavaBeanTypes.create(tpeMap).get());
        assertEquals(AOption.some(Map.class), JavaBeanTypes.rawType(tpeMap));

        assertEquals (new JavaBeanType(List.class), JavaBeanTypes.create (tpeWildcard).get());
        assertEquals (AOption.none (), JavaBeanTypes.create (tpeGeneric));

        assertEquals(AOption.some (List.class), JavaBeanTypes.rawType (tpeWildcard));
        assertEquals(AOption.none (), JavaBeanTypes.rawType (tpeGeneric));

    }

    @Test
    public void testPrimitives() {
        assertEquals(String.class, JavaBeanTypes.normalized(String.class));

        assertEquals(Boolean.class,   JavaBeanTypes.normalized(boolean.class));
        assertEquals(Character.class, JavaBeanTypes.normalized(char.class));
        assertEquals(Byte.class,      JavaBeanTypes.normalized(byte.class));
        assertEquals(Short.class,     JavaBeanTypes.normalized(short.class));
        assertEquals(Integer.class,   JavaBeanTypes.normalized(int.class));
        assertEquals(Long.class,      JavaBeanTypes.normalized(long.class));
        assertEquals(Float.class,     JavaBeanTypes.normalized(float.class));
        assertEquals(Double.class,    JavaBeanTypes.normalized(double.class));
    }
}
