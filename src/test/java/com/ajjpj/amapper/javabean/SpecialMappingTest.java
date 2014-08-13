package com.ajjpj.amapper.javabean;

import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.amapper.classes.ClassA;
import com.ajjpj.amapper.classes.ClassB;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;
import com.ajjpj.amapper.javabean.propbased.AExplicitPartialMapping;
import com.ajjpj.amapper.javabean.propbased.AGuardCondition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author arno
 */
public class SpecialMappingTest {

    @Test
    public void testSpecialMappingForward() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                        .addForwardSpecialMapping(new AExplicitPartialMapping<ClassA, ClassB, Object>() {
                            @Override public void doMap(ClassA source, ClassB target, AMapperWorker<?> worker, AMap<String, Object> context, APath path) throws Exception {
                                target.setFirstName("X. Y. Z.");
                                target.setLastName(source.getFirstName() + " " + source.getLastName());
                            }

                            @Override public void doDiff(ADiffBuilder diff, ClassA sourceOld, ClassA sourceNew, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
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
    public void testSpecialMappingBackward() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                        .addBackwardSpecialMapping(new AExplicitPartialMapping<ClassB, ClassA, Object>() {
                            @Override public void doMap(ClassB source, ClassA target, AMapperWorker<?> worker, AMap<String, Object> context, APath path) throws Exception {
                                target.setFirstName("X. Y. Z.");
                                target.setLastName(source.getFirstName() + " " + source.getLastName());
                            }

                            @Override public void doDiff(ADiffBuilder diff, ClassB sourceOld, ClassB sourceNew, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
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
    public void testGuardForward() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                        .withMatchingPropsMappings()
                        .withForwardGuardBySourceExpression("firstName", new AGuardCondition<ClassA, ClassB, Object>() {
                            @Override
                            public boolean shouldMap(ClassA source, ClassB target, Object helper, AMap<String, Object> context, APath path) {
                                return shouldMap;
                            }
                        })
                ).build();

        final ClassA o = new ClassA();
        o.setFirstName("Arno");
        o.setLastName("Haase");

        shouldMap = true;
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
    public void testGuardBackward() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                        .withMatchingPropsMappings()
                        .withBackwardGuardBySourceExpression("firstName", new AGuardCondition<ClassB, ClassA, Object>() {
                            @Override
                            public boolean shouldMap(ClassB source, ClassA target, Object helper, AMap<String, Object> context, APath path) {
                                return shouldMap;
                            }
                        })
                ).build();

        final ClassB o = new ClassB();
        o.setFirstName("Arno");
        o.setLastName("Haase");

        shouldMap = true;
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

