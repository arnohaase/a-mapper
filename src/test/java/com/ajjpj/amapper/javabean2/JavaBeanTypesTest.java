package com.ajjpj.amapper.javabean2;

import com.ajjpj.amapper.classes.ForTypeTests;
import com.ajjpj.amapper.javabean.SimpleJavaBeanType;
import org.junit.Test;

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

    @Test
    @SuppressWarnings("unchecked")
    public void testJavaType() throws Exception {
        final Type tpeString     = ForTypeTests.class.getMethod("withString").getGenericReturnType();
        final Type tpeStringList = ForTypeTests.class.getMethod("withStringList").getGenericReturnType();
        final Type tpeMap        = ForTypeTests.class.getMethod("withMap").getGenericReturnType();

        assertEquals(new JavaBeanType(String.class), JavaBeanTypes.create(tpeString));
        assertEquals(new SingleParamBeanType(List.class, String.class), JavaBeanTypes.create(tpeStringList));

        assertEquals(String.class, JavaBeanTypes.rawType(tpeString));
        assertEquals(List.class,   JavaBeanTypes.rawType(tpeStringList));

        // There is no *built-in* special handling of Java types with more than one parameter, especially in generic factories. Using code
        //  is however free to provide their own implementation of JavaBeanType that provide such support
        assertEquals(new JavaBeanType(Map.class), JavaBeanTypes.create(tpeMap));
        assertEquals(Map.class, JavaBeanTypes.rawType(tpeMap));
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
