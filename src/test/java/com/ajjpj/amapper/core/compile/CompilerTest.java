package com.ajjpj.amapper.core.compile;

import com.ajjpj.amapper.classes.ClassA;
import com.ajjpj.amapper.classes.ClassB;
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
    public void testCompile() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class).withMatchingPropsMappings())
                .build(true);

        final ClassA a = new ClassA();
        final ClassB b = mapper.map(a, ClassB.class);
        assertNotNull(b);
    }

    @Test
    public void testTodo() {
        fail("TODO: debug logging of compiled mapping defs instead of System.out.println()");
        fail("TODO: java code for accessors: method path, field, ognl");
        fail("TODO: deferred mapping for prop based");
        fail("TODO: diff");
        fail("TODO: comprehensive tests");
    }
}

