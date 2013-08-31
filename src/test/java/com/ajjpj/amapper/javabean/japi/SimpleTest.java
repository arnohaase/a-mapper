package com.ajjpj.amapper.javabean.japi;

import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.japi.classes.*;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author arno
 */
public class SimpleTest {
    @Test
    public void testSimple() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(Person.class, Person.class))
                .build();

        final Person p = new Person("Arno", "Haase");
        final Person mapped = mapper.map(p, Person.class);

        assertTrue(mapped != p);
        assertEquals(p.getFirstName(), mapped.getFirstName());
        assertEquals(p.getLastName(), mapped.getLastName());
    }

    @Test
    public void testSimple2 () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class))
            .withBeanMapping(JavaBeanMapping.create(InnerClassA.class, InnerClassB.class))
            .build();

        final ClassA a = new ClassA();
        a.setFirstName ("Heino");
        a.setLastName ("Mustermann");
        a.setNumChildren(99);
        a.setBirthday (new Date(1234567));
        a.setE (MapperTestEnum.b);
        a.getPhone().add (new InnerClassA ("123", "a"));
        a.getPhone().add (new InnerClassA("456", "b"));

        final ClassB b = mapper.map (a, ClassB.class);

        assertEquals ("Heino", b.getFirstName());
        assertEquals ("Mustermann", b.getLastName());
        assertEquals (99, b.getNumChildren());
        assertEquals(new Date(1234567), b.getBirthday());
        assertSame (MapperTestEnum.b, b.getE());

        assertEquals (2, b.getPhone().size());
        assertEquals ("123", b.getPhone().get(0).getPhone());
        assertEquals ("a",   b.getPhone().get(0).getOther());
        assertEquals ("456", b.getPhone().get(1).getPhone());
        assertEquals ("b",   b.getPhone().get(1).getOther());
    }



    //TODO convenience superclass for object mappings
    //TODO convenience superclass for logger (?)
}
