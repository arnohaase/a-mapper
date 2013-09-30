package com.ajjpj.amapper.javabean.japi;

import com.ajjpj.amapper.core.ADiffBuilder;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.PathBuilder;
import com.ajjpj.amapper.javabean.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.classes.ClassA;
import com.ajjpj.amapper.classes.ClassB;
import com.ajjpj.amapper.javabean.propbased.ExplicitPartialMapping;
import com.ajjpj.amapper.javabean.propbased.ShouldMap;
import org.junit.Test;
import scala.collection.immutable.Map;

import static org.junit.Assert.*;

/**
 * @author arno
 */
public class SpecialMappingTest {
    @Test
    public void testFail() {
        fail("todo");
    }

    @Test
    public void testSpecialMappingForward() {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                        .withForwardSpecialMapping(new ExplicitPartialMapping<ClassA, ClassB>() {
                            @Override
                            public void doMap(ClassA source, ClassB target, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> context, PathBuilder path) {
                                target.setFirstName("X. Y. Z.");
                                target.setLastName(source.getFirstName() + " " + source.getLastName());
                            }

                            @Override
                            public void doDiff(ADiffBuilder diff, ClassA sourceOld, ClassA sourceNew, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> contextOld, Map<String, Object> contextNew, PathBuilder path, boolean isDerived) {
                            }
                        })
                ).build();

        final ClassA o = new ClassA();
        o.setFirstName("Arno");
        o.setLastName("Haase");
        final ClassB mapped = mapper.map(o, ClassB.class);

        assertEquals("X. Y. Z.", mapped.getFirstName());
        assertEquals("Arno Haase", mapped.getLastName());
    }

    @Test
    public void testSpecialMappingBackward() {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                        .withBackwardSpecialMapping(new ExplicitPartialMapping<ClassB, ClassA>() {
                            @Override
                            public void doMap(ClassB source, ClassA target, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> context, PathBuilder path) {
                                target.setFirstName("X. Y. Z.");
                                target.setLastName(source.getFirstName() + " " + source.getLastName());
                            }

                            @Override
                            public void doDiff(ADiffBuilder diff, ClassB sourceOld, ClassB sourceNew, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> contextOld, Map<String, Object> contextNew, PathBuilder path, boolean isDerived) {
                            }
                        })
                ).build();

        final ClassB o = new ClassB();
        o.setFirstName("Arno");
        o.setLastName("Haase");
        final ClassA mapped = mapper.map(o, ClassA.class);

        assertEquals("X. Y. Z.", mapped.getFirstName());
        assertEquals("Arno Haase", mapped.getLastName());
    }

    boolean shouldMap = true;

    @Test
    public void testGuardForward() {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                        .withMatchingPropsMappings()
                        .withForwardGuardBySourceExpression("firstName", new ShouldMap<ClassA,ClassB>() {
                            @Override
                            public boolean shouldMap(ClassA source, ClassB target, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> context, PathBuilder path) {
                                return shouldMap;
                            }
                        })
                ).build();

        final ClassA o = new ClassA();
        o.setFirstName("Arno");
        o.setLastName("Haase");

        final ClassB mapped1 = new ClassB();
        mapped1.setFirstName("old first name");
        mapper.map(o, mapped1);
        assertEquals("Arno", mapped1.getFirstName());
        assertEquals("Haase", mapped1.getLastName());

        shouldMap = false;
        final ClassB mapped2 = new ClassB();
        mapped2.setFirstName("old first name");
        mapper.map(o, mapped2);
        assertEquals("old first name", mapped2.getFirstName());
        assertEquals("Haase", mapped2.getLastName());

        final ClassA mappedBack = mapper.map(mapped1, ClassA.class);
        assertEquals("Arno", mappedBack.getFirstName());
    }

    @Test
    public void testGuardBackward() {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                        .withMatchingPropsMappings()
                        .withBackwardGuardBySourceExpression("firstName", new ShouldMap<ClassB, ClassA>() {
                            @Override
                            public boolean shouldMap(ClassB source, ClassA target, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> context, PathBuilder path) {
                                return shouldMap;
                            }
                        })
                ).build();

        final ClassB o = new ClassB();
        o.setFirstName("Arno");
        o.setLastName("Haase");

        final ClassA mapped1 = new ClassA();
        mapped1.setFirstName("old first name");
        mapper.map(o, mapped1);
        assertEquals("Arno", mapped1.getFirstName());
        assertEquals("Haase", mapped1.getLastName());

        shouldMap = false;
        final ClassA mapped2 = new ClassA();
        mapped2.setFirstName("old first name");
        mapper.map(o, mapped2);
        assertEquals("old first name", mapped2.getFirstName());
        assertEquals("Haase", mapped2.getLastName());

        final ClassB mappedBack = mapper.map(mapped1, ClassB.class);
        assertEquals("Arno", mappedBack.getFirstName());
    }
}

