package com.ajjpj.amapper.javabean;

import com.ajjpj.amapper.classes.*;
import com.ajjpj.amapper.classes.duplicateidentity.ChildA;
import com.ajjpj.amapper.classes.duplicateidentity.ChildB;
import com.ajjpj.amapper.classes.duplicateidentity.ParentA;
import com.ajjpj.amapper.classes.duplicateidentity.ParentB;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;
import org.junit.Test;

import static org.junit.Assert.*;


public class PropertyBasedMappingTest {
    @Test
    public void testBidirectional () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class).withMatchingPropsMappings())
            .build();

        final ClassA a = new ClassA();
        a.setFirstName("first");

        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals("first", b.getFirstName());

        final ClassA a2 = mapper.map(b, ClassA.class);
        assertEquals("first", a2.getFirstName());
    }

    @Test
    public void testBidirectionalHeuristic () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                    .addMapping ("firstName", "firstName")
            )
            .build();

        final ClassA a = new ClassA();
        a.setFirstName("first");

        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals("first", b.getFirstName());

        final ClassA a2 = mapper.map(b, ClassA.class);
        assertEquals("first", a2.getFirstName());
    }

    @Test public void testHeuristicCascade () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withBeanMapping (JavaBeanMapping.create (ParentA.class, ParentB.class)
                        .addMapping ("child.name", "child.name2")
                )
                .build ();

        final ParentA a = new ParentA();
        a.setChild (new ChildA ());
        a.getChild ().setName ("the name");

        final ParentB b = new ParentB();
        b.setChild (new ChildB ());

        mapper.map (a, b);

        assertEquals ("the name", b.getChild ().getName2 ());
    }

    @Test public void testHeuristicCascadeNullSafe () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withBeanMapping (JavaBeanMapping.create (ParentA.class, ParentB.class)
                        .addMapping ("child.?name", "child.?name2")
                )
                .build ();

        final ParentA a = new ParentA();
        a.setChild (new ChildA ());
        a.getChild ().setName ("the name");

        assertEquals (null, mapper.map (a, ParentB.class).getChild ());

        final ParentB b = new ParentB ();
        b.setChild (new ChildB ());
        b.getChild ().setName2 ("original name");

        mapper.map (new ParentA(), b);
        assertEquals (null, b.getChild ().getName2 ());
    }

    @Test public void testHeuristicDeferred () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(ClassCyclicParent.class, ClassCyclicParent.class)
                        .addMapping ("child", "child")
                )
                .withBeanMapping (JavaBeanMapping.create(ClassCyclicChild.class, ClassCyclicChild.class)
                        .addMapping ("parent", "parent")
                )
                .build();

        final ClassCyclicParent parent = new ClassCyclicParent();
        final ClassCyclicChild child = new ClassCyclicChild();

        parent.setChild (child);
        child.setParent (parent);

        final ClassCyclicParent mappedParent = mapper.map (parent, ClassCyclicParent.class);

        assertNotSame (parent, mappedParent);
        assertSame (mappedParent, mappedParent.getChild().getParent());
    }

    @Test
    public void testRemove () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                .removeMappingForSourceProp("firstName")
            )
            .build();

        final ClassA a = new ClassA();
        a.setFirstName("first");

        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals(null, b.getFirstName());
    }

    @Test
    public void testAddOneWay () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                .removeMappingForSourceProp("firstName")
                .removeMappingForSourceProp ("lastName")
                .addOneWayMapping ("firstName", String.class, "lastName", String.class)
            )
            .build();

        final ClassA a = new ClassA();
        a.setFirstName("first");

        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals ("first", b.getLastName ());

        final ClassA mappedA = mapper.map(b, ClassA.class);
        assertEquals(null, mappedA.getFirstName());
    }

    @Test
    public void testAddOneWayHeuristic () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
            .withBeanMapping(JavaBeanMapping.create (ClassA.class, ClassB.class)
                .addOneWayMapping ("firstName", "lastName")
            )
            .build();

        final ClassA a = new ClassA();
        a.setFirstName("first");

        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals ("first", b.getLastName ());

        final ClassA mappedA = mapper.map(b, ClassA.class);
        assertEquals(null, mappedA.getFirstName());
    }

    @Test
    public void testAddBackwardsOneWay () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                .removeMappingForSourceProp("firstName")
                .removeMappingForSourceProp ("lastName")
                .addBackwardsOneWayMapping ("firstName", String.class, "lastName", String.class)
            )
            .build ();

        final ClassA a = new ClassA();
        a.setFirstName("first a");
        a.setLastName("last a");

        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals(null, b.getFirstName());
        assertEquals(null, b.getLastName());

        b.setFirstName("first b");
        b.setLastName("last b");
        final ClassA mappedA = mapper.map(b, ClassA.class);
        assertEquals("last b", mappedA.getFirstName());
        assertEquals(null, mappedA.getLastName ());
    }

    @Test
    public void testAddBackwardsOneWayHeuristic () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
            .withBeanMapping(JavaBeanMapping.create (ClassA.class, ClassB.class)
                .addBackwardsOneWayMapping("firstName", "lastName")
            )
            .build ();

        final ClassA a = new ClassA();
        a.setFirstName("first a");
        a.setLastName("last a");

        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals(null, b.getFirstName());
        assertEquals(null, b.getLastName());

        b.setFirstName("first b");
        b.setLastName("last b");
        final ClassA mappedA = mapper.map(b, ClassA.class);
        assertEquals("last b", mappedA.getFirstName());
        assertEquals(null, mappedA.getLastName());
    }

    @Test
    public void testMakeOneWay () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
            .withObjectMapping (BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
            .withBeanMapping (JavaBeanMapping.create (ClassA.class, ClassB.class)
                    .withMatchingPropsMappings ()
                    .makeOneWay ("firstName")
            )
             .build();

        final ClassA a = new ClassA ();
        a.setFirstName("Fritz");

        final ClassB b = new ClassB ();
        mapper.map (a, b);
        assertEquals ("Fritz", b.getFirstName());

        b.setFirstName("Fred");
        mapper.map (b, a);
        assertEquals ("Fritz", a.getFirstName());
    }

    @Test
    public void testMakeBackwardsOneWay () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
            .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                .withMatchingPropsMappings()
                .makeBackwardsOneWay("firstName")
            )
             .build();

        final ClassA a = new ClassA ();
        a.setFirstName("Fritz");

        final ClassB b = new ClassB ();
        mapper.map (a, b);
        assertEquals (null, b.getFirstName());

        b.setFirstName("Fred");
        mapper.map (b, a);
        assertEquals ("Fred", a.getFirstName());
    }

    @Test
    public void testOverridesAndOgnl () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                    .removeMappingForSourceProp("phone")
                    .overrideWithOneWayMapping("phone[0].other", String.class, "lastName", String.class)
                    .overrideMapping("lastName", String.class, "firstName", String.class)
                    .overrideWithOneWayMapping("phone.size()", Integer.class, "numChildren", Long.class)
            )
            .build();

        final ClassA a = new ClassA ();
        a.setLastName("Mustermann");
        a.getPhone().add (new InnerClassA ("123", "other1"));
        a.getPhone().add (new InnerClassA("456", "other2"));
        a.getPhone().add (new InnerClassA ("789", "other3"));

        final ClassB b = mapper.map (a, ClassB.class);

        assertTrue (b.getPhone().isEmpty());
        assertEquals ("other1", b.getLastName());
        assertEquals ("Mustermann", b.getFirstName());
        assertEquals (3L, b.getNumChildren());

        final ClassA mappedA = new ClassA ();
        mappedA.getPhone().add (new InnerClassA("a", "b"));
        mapper.map (b, mappedA);
//        assertEquals ("other1", mappedA.getPhone().get(0).getOther());
        assertEquals ("Mustermann", mappedA.getLastName());
    }

    @Test
    public void testSameSource() throws Exception {
        // test that a single field can be mapped to several fields
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
            .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                .withMatchingPropsMappings()
                .removeMappingForTargetProp("lastName")
                .addMapping ("firstName", String.class, "lastName", String.class)
        )
        .build ();

        final ClassA a = new ClassA ();
        a.setFirstName ("first");

        final ClassB mapped = mapper.map (a, ClassB.class);
        assertEquals("first", mapped.getFirstName ());
        assertEquals("first", mapped.getLastName ());
    }
}
