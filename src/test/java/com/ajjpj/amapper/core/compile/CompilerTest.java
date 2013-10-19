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
        final APropertyBasedObjectMappingDef om = JavaBeanMapping.create(ClassA.class, ClassB.class).withMatchingPropsMappings().build();

        final AMappingDefCompiler compiler = new AMappingDefCompiler(Arrays.asList(om), Arrays.asList(BuiltinValueMappingDefs.StringMappingDef));
        final AObjectMappingDef compiled = compiler.getCompiledObjectMappingDefs().iterator().next();

        assertTrue (compiled.canHandle(new AQualifiedSourceAndTargetType(JavaBeanTypes.create(ClassA.class), AQualifier.NO_QUALIFIER, JavaBeanTypes.create(ClassB.class), AQualifier.NO_QUALIFIER)));
        assertFalse(compiled.canHandle(new AQualifiedSourceAndTargetType(JavaBeanTypes.create(ClassA.class), AQualifier.NO_QUALIFIER, JavaBeanTypes.create(ClassA.class), AQualifier.NO_QUALIFIER)));
        assertFalse(compiled.canHandle(new AQualifiedSourceAndTargetType(JavaBeanTypes.create(ClassB.class), AQualifier.NO_QUALIFIER, JavaBeanTypes.create(ClassB.class), AQualifier.NO_QUALIFIER)));

        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
                .withObjectMapping(compiled)
                .build();

        final ClassA a = new ClassA();
        final ClassB b = mapper.map(a, ClassB.class);
        assertNotNull(b);
    }

    @Test
    public void testTodo() {
        fail("TODO: java code for accessors: method path, field, ognl");
        fail("TODO: inline inlineable value mapping defs");
        fail("TODO: deferred mapping for prop based");
        fail("TODO: diff");
    }
}

