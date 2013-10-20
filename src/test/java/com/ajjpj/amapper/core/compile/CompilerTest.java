package com.ajjpj.amapper.core.compile;

import com.ajjpj.amapper.classes.*;
import com.ajjpj.amapper.core.diff.ADiff;
import com.ajjpj.amapper.core.diff.ADiffElement;
import com.ajjpj.amapper.javabean.JavaBeanMapper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * @author arno
 */
public class CompilerTest {
    @Test
    public void testSimpleCompile() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class).withMatchingPropsMappings())
                .build(true);

        final ClassA a = new ClassA();
        a.setFirstName("Arno");
        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals("Arno", b.getFirstName());
    }

    @Test
    public void testCompileOgnl() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class).addOneWayMapping("1>0 ? firstName : lastName", String.class, "1>0 ? firstName : lastName", String.class))
                .build(true);

        final ClassA a = new ClassA();
        a.setFirstName("Arno");
        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals("Arno", b.getFirstName());
    }

    @Test
    public void testCompileMethodPath() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(WithProperties.class, WithProperties.class).addOneWayMapping("other.withProperties.xyz", String.class, "other.withProperties.xyz", String.class))
                .build(true);

        final WithProperties root = new WithProperties();
        root.getOther().inner = root;
        root.setXyz("Abc");

        final WithProperties mapped = new WithProperties();
        mapped.getOther().inner = mapped;

        mapper.map(root, mapped);
        assertEquals("Abc", mapped.getXyz());
    }

    @Test
    public void testCompileMethodPathMiddleNullGet() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(WithProperties.class, WithProperties.class).addOneWayMapping("other.?withProperties.xyz", String.class, "other.withProperties.xyz", String.class))
                .build(true);

        final WithProperties root = new WithProperties();
        root.inner = null;

        final WithProperties mapped = new WithProperties();
        mapped.getOther().inner = mapped;
        mapped.setXyz("orig");

        mapper.map(root, mapped);
        assertEquals(null, mapped.getXyz());
    }

    @Test
    public void testCompileMethodPathMiddleNullGetExc() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(WithProperties.class, WithProperties.class).addOneWayMapping("other.withProperties.xyz", String.class, "other.withProperties.xyz", String.class))
                .build(true);

        final WithProperties root = new WithProperties();
        root.inner = null;

        final WithProperties mapped = new WithProperties();
        mapped.getOther().inner = mapped;

        try {
            mapper.map(root, mapped);
            fail("exception expected");
        }
        catch(Exception exc) {
        }
    }

    @Test
    public void testCompileMethodPathMiddleNullSet() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(WithProperties.class, WithProperties.class).addOneWayMapping("other.withProperties.xyz", String.class, "other.?withProperties.xyz", String.class))
                .build(true);

        final WithProperties root = new WithProperties();
        root.getOther().inner = root;
        root.setXyz("Abc");

        final WithProperties mapped = new WithProperties();
        mapped.inner = null;

        mapper.map(root, mapped);
    }

    @Test
    public void testCompileMethodPathMiddleNullSetExc() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(WithProperties.class, WithProperties.class).addOneWayMapping("other.withProperties.xyz", String.class, "other.withProperties.xyz", String.class))
                .build(true);

        final WithProperties root = new WithProperties();
        root.getOther().inner = root;
        root.setXyz("Abc");

        final WithProperties mapped = new WithProperties();
        mapped.inner = null;

        try {
            mapper.map(root, mapped);
            fail("exception expected");
        }
        catch (Exception exc) {
        }
    }

    @Test
    public void testCompileMethodPathFinalNullGet() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(WithProperties.class, WithProperties.class).addOneWayMapping("other.withProperties.?xyz", String.class, "other.withProperties.xyz", String.class))
                .build(true);

        final WithProperties root = new WithProperties();

        final WithProperties mapped = new WithProperties();
        mapped.getOther().inner = mapped;
        mapped.setXyz("orig");

        mapper.map(root, mapped);
        assertEquals(null, mapped.getXyz());
    }

    @Test
    public void testCompileMethodPathFinalNullGetExc() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(WithProperties.class, WithProperties.class).addOneWayMapping("other.withProperties.xyz", String.class, "other.withProperties.xyz", String.class))
                .build(true);

        final WithProperties root = new WithProperties();

        final WithProperties mapped = new WithProperties();
        mapped.getOther().inner = mapped;

        try {
            mapper.map(root, mapped);
            fail("exception expected");
        }
        catch (Exception exc) {
        }
    }

    @Test
    public void testCompileMethodPathFinalNullSet() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(WithProperties.class, WithProperties.class).addOneWayMapping("other.withProperties.xyz", String.class, "other.withProperties.?xyz", String.class))
                .build(true);

        final WithProperties root = new WithProperties();
        root.getOther().inner = root;
        root.setXyz("Abc");

        final WithProperties mapped = new WithProperties();

        mapper.map(root, mapped);
        assertEquals(null, mapped.getXyz());
    }

    @Test
    public void testCompileMethodPathFinalNullSetExc() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(WithProperties.class, WithProperties.class).addOneWayMapping("other.withProperties.xyz", String.class, "other.withProperties.xyz", String.class))
                .build(true);

        final WithProperties root = new WithProperties();
        root.getOther().inner = root;
        root.setXyz("Abc");

        final WithProperties mapped = new WithProperties();

        try {
            mapper.map(root, mapped);
            fail("exception expected");
        }
        catch(Exception exc) {
        }
    }

    @Test
    public void testDiff() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(BuiltinCollectionMappingDefs.SetByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(SourceParentWithId.class, TargetParentWithId.class)
                        .addMapping("sourceId", Integer.class, "targetId", Integer.class)
                        .addMapping("sourceAttrib", String.class, "targetAttrib", String.class)
                        .addMapping("sourceChild", SourceChildWithId.class, "targetChild", TargetChildWithId.class)
                )
                .withBeanMapping(JavaBeanMapping.create(SourceChildWithId.class, TargetChildWithId.class)
                        .addMapping("sourceId", Integer.class, "targetId", Integer.class)
                        .addMapping("sourceAttrib1", String.class, "targetAttrib1", String.class)
                        .addMapping("sourceAttrib2", Integer.class, "targetAttrib2", Integer.class)
                )
                .build(true);

        final SourceParentWithId source1 = new SourceParentWithId(1, "a");
        source1.setSourceChild(new SourceChildWithId(2, "b", 3));

        final SourceParentWithId source2 = new SourceParentWithId(1, "b");
        source2.setSourceChild(new SourceChildWithId(3, "b", 4));

        final ADiff diff = mapper.diff(source1, source2, SourceParentWithId.class, TargetParentWithId.class);
        assertEquals(4, diff.getElements().size());

        assertEquals(ADiffElement.Kind.Attribute, diff.getSingle("targetAttrib").get().kind);
        assertEquals("a", diff.getSingle("targetAttrib").get().oldValue);
        assertEquals("b", diff.getSingle("targetAttrib").get().newValue);
        assertEquals(false, diff.getSingle("targetAttrib").get().isDerived);

        assertEquals(ADiffElement.Kind.RefChange, diff.getSingle("targetChild").get().kind);
        assertEquals("SourceChild (2)", diff.getSingle("targetChild").get().oldValue);
        assertEquals("SourceChild (3)", diff.getSingle("targetChild").get().newValue);
        assertEquals(false, diff.getSingle("targetChild").get().isDerived);

        assertEquals(ADiffElement.Kind.Attribute, diff.getSingle("targetChild.targetId").get().kind);
        assertEquals(2, diff.getSingle("targetChild.targetId").get().oldValue);
        assertEquals(3, diff.getSingle("targetChild.targetId").get().newValue);
        assertEquals(true, diff.getSingle("targetChild.targetId").get().isDerived);

        assertEquals(ADiffElement.Kind.Attribute, diff.getSingle("targetChild.targetAttrib2").get().kind);
        assertEquals(3, diff.getSingle("targetChild.targetAttrib2").get().oldValue);
        assertEquals(4, diff.getSingle("targetChild.targetAttrib2").get().newValue);
        assertEquals(true, diff.getSingle("targetChild.targetAttrib2").get().isDerived);
    }
}

