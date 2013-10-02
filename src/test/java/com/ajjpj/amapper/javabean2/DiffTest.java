package com.ajjpj.amapper.javabean2;

import com.ajjpj.amapper.classes.ClassA;
import com.ajjpj.amapper.classes.ClassB;
import com.ajjpj.amapper.classes.InnerClassA;
import com.ajjpj.amapper.classes.InnerClassB;
import com.ajjpj.amapper.core2.diff.ADiff;
import com.ajjpj.amapper.core2.diff.ADiffElement;
import com.ajjpj.amapper.core2.exclog.AMapperLogger;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.path.APathSegment;
import com.ajjpj.amapper.javabean2.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean2.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean2.mappingdef.BuiltinCollectionMappingDefs;
import com.ajjpj.amapper.util.coll.AOption;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;


/**
 * @author arno
 */
public class DiffTest {
    @Test
    public void testSimpleDiff() throws Exception {
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
        assertEquals (2, diff.getElements().size());
        assertTrue(diff.getPaths().contains(APath.EMPTY));
        assertTrue (diff.getPaths().contains(new APath(APathSegment.simple("firstName"))));

        final AOption<ADiffElement> firstNameDiff = diff.getSingle(new APath(APathSegment.simple("firstName")));
        assertEquals(true, firstNameDiff.isDefined());
        assertEquals("Arno", firstNameDiff.get().oldValue);
        assertEquals("Fred", firstNameDiff.get().newValue);
        assertEquals(true, firstNameDiff.get().isDerived);

        assertEquals(firstNameDiff, diff.getSingle("firstName"));
    }

    @Test
    public void testTwoStepDiff() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class).withMatchingPropsMappings())
                .withBeanMapping(JavaBeanMapping.create(InnerClassA.class, InnerClassB.class).withMatchingPropsMappings())
                .build();

        final ClassA a1 = new ClassA();
        final ClassA a2 = new ClassA();

        final InnerClassA inner1 = new InnerClassA("phone1", "other1");
        final InnerClassA inner2 = new InnerClassA("phone2", "other2");

        a1.getPhone().add(inner1);
        a2.getPhone().add(inner2);

        final ADiff diff = mapper.diff(a1, a2, ClassA.class, ClassB.class);

        System.out.println(diff);
        fail("todo");
    }
}
