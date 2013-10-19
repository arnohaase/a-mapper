package com.ajjpj.amapper.core.compile;

import com.ajjpj.amapper.classes.ClassA;
import com.ajjpj.amapper.classes.ClassB;
import com.ajjpj.amapper.classes.WithProperties;
import com.ajjpj.amapper.core.AObjectMappingDef;
import com.ajjpj.amapper.core.AValueMappingDef;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.javabean.JavaBeanMapper;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinValueMappingDefs;
import com.ajjpj.amapper.javabean.propbased.APropertyBasedObjectMappingDef;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;


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
    public void testTodo() {
        fail("TODO: diff");
    }
}

