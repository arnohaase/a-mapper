package com.ajjpj.amapper.javabean;

import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.amapper.classes.*;
import com.ajjpj.amapper.core.APreProcessor;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;
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
                .withBeanMapping(JavaBeanMapping.create(Person.class, Person.class).withMatchingPropsMappings())
                .build();

        final Person p = new Person("Arno", "Haase");
        final Person mapped = mapper.map(p, Person.class);

        assertTrue(mapped != p);
        assertEquals(p.getFirstName(), mapped.getFirstName());
        assertEquals(p.getLastName(), mapped.getLastName());
    }

    @Test
    public void testSimple2 () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
            .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class).withMatchingPropsMappings())
            .withBeanMapping(JavaBeanMapping.create(InnerClassA.class, InnerClassB.class).withMatchingPropsMappings())
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

    @Test public void testPreprocessInitialArgument() throws Exception {
        final APreProcessor unwrapper = new APreProcessor () {
            @Override public boolean canHandle (AQualifiedSourceAndTargetType types) throws Exception {
                return true;
            }
            @SuppressWarnings ("unchecked")
            @Override public <T> AOption<T> preProcess (T o, AQualifiedSourceAndTargetType qt) {
                if (o instanceof ClassAWrapper) {
                    return AOption.some ((T) ((ClassAWrapper) o).getInner ());
                }

                return AOption.some (o);
            }
        };

        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withPreProcessor (unwrapper)
                .withObjectMapping (BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class).withMatchingPropsMappings())
                .withBeanMapping(JavaBeanMapping.create(InnerClassA.class, InnerClassB.class).withMatchingPropsMappings())
                .build();

        final ClassA a = new ClassA ();
        a.setFirstName ("first name");

        assertEquals ("first name", mapper.map (new ClassAWrapper (a), ClassB.class).getFirstName ());
        assertEquals ("first name", mapper.map (new ClassAWrapper (a), new ClassB()).getFirstName ());
    }

    static class ClassAWrapper {
        final ClassA inner;

        ClassAWrapper (ClassA inner) {
            this.inner = inner;
        }

        public ClassA getInner () {
            return inner;
        }
    }

    @Test public void testPreprocessInitialArgumentFilterOut() throws Exception {
        final APreProcessor remover = new APreProcessor () {
            @Override public boolean canHandle (AQualifiedSourceAndTargetType types) throws Exception {
                return true;
            }
            @Override public <T> AOption<T> preProcess (T o, AQualifiedSourceAndTargetType qt) {
                return AOption.none();
            }
        };

        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withPreProcessor (remover)
                .withObjectMapping (BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class).withMatchingPropsMappings())
                .withBeanMapping(JavaBeanMapping.create(InnerClassA.class, InnerClassB.class).withMatchingPropsMappings())
                .build();

        assertNull (mapper.map (new ClassA (), ClassB.class));
        assertNull (mapper.map (new ClassA (), new ClassB ()));
    }
}
