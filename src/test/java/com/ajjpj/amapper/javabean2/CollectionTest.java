package com.ajjpj.amapper.javabean2;

import com.ajjpj.amapper.classes.ClassA;
import com.ajjpj.amapper.javabean2.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean2.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean2.mappingdef.BuiltinCollectionMappingDefs;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


public class CollectionTest {
    @Test
    public void testSet() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withObjectMapping(BuiltinCollectionMappingDefs.SetByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassA.class))
            .build();

        final Set<ClassA> set = new HashSet<ClassA>();
        final ClassA a1 = new ClassA();
        a1.setFirstName("A");
        set.add(a1);
        final ClassA a2 = new ClassA();
        a2.setFirstName("B");
        set.add(a2);

        @SuppressWarnings("unchecked")
        final Set<ClassA> mapped = mapper.map(set, Set.class, ClassA.class, null, Set.class, ClassA.class);

        assertEquals(2, mapped.size());
    }

    @Test
    public void testListIsMerged() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
                .build();

        // Verify that the string's identity is preserved. In a real-world application, this is weird behavior for
        //  dealing with strings, but this illustrates the point for dealing with 'real' objects.
        final String a = new String("a");
        final List<String> l = new ArrayList<String>();
        l.add("b");
        l.add(a);
        l.add("c");

        final List<String> mapped = mapper.mapList(Arrays.asList("a", "b", "d"), String.class, l, String.class);

        assertSame(l, mapped);
        assertEquals(3, l.size());
        assertTrue(l.contains("a"));
        assertTrue(l.contains("b"));
        assertTrue(l.contains("d"));

        final String aFromResult = l.get(l.indexOf("a"));
        assertSame(a, aFromResult);
    }

    @Test
    public void testListAsSet() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassA.class))
            .build();

        final List<ClassA> source1 = Arrays.asList(new ClassA(), new ClassA());

        @SuppressWarnings("unchecked")
        final List<ClassA> target1 = mapper.map (source1, List.class, ClassA.class, null, List.class, ClassA.class);
        assertEquals (2, target1.size());
        assertTrue (target1.get(0).getClass() == ClassA.class);
        assertTrue (target1.get(1).getClass() == ClassA.class);

        assertNotSame (target1.get(0), target1.get(1));

        final ClassA a = new ClassA();
        final List<ClassA> source2 = Arrays.asList(a, a);

        @SuppressWarnings("unchecked")
        final List<ClassA> target2 = mapper.map (source2, List.class, ClassA.class, null, List.class, ClassA.class);
        assertEquals (1, target2.size());
        assertTrue (target2.get(0).getClass() == ClassA.class);
    }
}
