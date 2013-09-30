package com.ajjpj.amapper.javabean.japi;

import com.ajjpj.amapper.core.*;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.classes.ClassA;
import com.ajjpj.amapper.classes.ClassB;
import org.junit.Test;
import scala.Option;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;


/**
 * @author arno
 */
public class DiffTest {
    @Test
    public void testDiff() {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                        .addMapping("firstName", String.class, "firstName", String.class)
                        .addMapping("lastName", String.class, "lastName", String.class))
                .build();

        final ClassA  a1 = new ClassA();
        final ClassA a2 = new ClassA();

        a1.setFirstName("Arno");
        a1.setLastName("Haase");

        a2.setFirstName("Fred");
        a2.setLastName("Haase");

        final ADiff diff = mapper.diff(a1, a2, ClassA.class, ClassB.class);
        assertEquals (2, diff.elements().size());
        assertTrue (diff.paths().contains(new APath(new ArrayList<PathSegment>())));
        assertTrue (diff.paths().contains(new APath(Arrays.asList(new SimplePathSegment("firstName")))));

        final Option<ADiffElement> firstNameDiff = diff.getSingle(new APath(Arrays.asList(new SimplePathSegment("firstName"))));
        assertEquals(true, firstNameDiff.isDefined());
        assertEquals("Arno", firstNameDiff.get().oldValue());
        assertEquals("Fred", firstNameDiff.get().newValue());
        assertEquals(true, firstNameDiff.get().isDerived());

        assertEquals(firstNameDiff, diff.getSingle("firstName"));
    }
}
