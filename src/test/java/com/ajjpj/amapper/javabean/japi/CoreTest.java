package com.ajjpj.amapper.javabean.japi;

import com.ajjpj.amapper.core.*;
import com.ajjpj.amapper.javabean.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.SingleParamBeanType;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.japi.classes.ClassCyclicChild;
import com.ajjpj.amapper.javabean.japi.classes.ClassCyclicParent;
import org.junit.Test;
import scala.collection.immutable.Map;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


public class CoreTest {
    @Test
    public void testCyclicRef () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withBeanMapping (JavaBeanMapping.create (ClassCyclicParent.class, ClassCyclicParent.class))
            .withBeanMapping (JavaBeanMapping.create (ClassCyclicChild.class, ClassCyclicChild.class))
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
    public void testCyclicRefWithList () throws Exception {
        final ClassCyclicParent parent = new ClassCyclicParent();
        final ClassCyclicChild child1 = new ClassCyclicChild();
        final ClassCyclicChild child2 = new ClassCyclicChild();

        parent.getChildList().add(child1);
        parent.getChildList().add(child2);
        child1.setParent (parent);
        child2.setParent (parent);

        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withBeanMapping (JavaBeanMapping.create (ClassCyclicParent.class, ClassCyclicParent.class))
            .withBeanMapping (JavaBeanMapping.create (ClassCyclicChild.class, ClassCyclicChild.class))
            .build ();

        final ClassCyclicParent mappedParent = mapper.map (parent, ClassCyclicParent.class);

        assertNotSame (parent, mappedParent);
        assertSame (mappedParent, mappedParent.getChildList().get(0).getParent());
    }

    @Test
    public void testObjectMappingPerElementClass () {
        final AObjectMappingDef<List<?>, List<?>, JavaBeanMappingHelper> stringListMapping = new AObjectMappingDef<List<?>, List<?>, JavaBeanMappingHelper> () {
            @Override
            public boolean canHandle(AType st, AQualifier sourceQualifier, AType tt, AQualifier targetQualifier) {
                if(st instanceof SingleParamBeanType && tt instanceof SingleParamBeanType) {
                    final SingleParamBeanType sourceType = (SingleParamBeanType) st;
                    final SingleParamBeanType targetType = (SingleParamBeanType) tt;

                    return sourceType.cls() == List.class && sourceType.paramCls() == Integer.class && targetType.cls() == List.class && targetType.paramCls() == String.class;
                }

                return false;
            }

            @Override
            public boolean isCacheable() {
                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public List<?> map(List<?> source, AType sourceType, AQualifier sourceQualifier, List<?> target, AType targetType, AQualifier targetQualifier, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> context, PathBuilder path) {
                final LinkedList<String> result = new LinkedList<String>();
                for (Integer i: (Collection<Integer>) source) {
                    result.add ("number " + i);
                }
                return result;
            }
        };

        final AValueMappingDef<Number, String, JavaBeanMappingHelper> numberToString = new AValueMappingDef<Number, String, JavaBeanMappingHelper>() {
            @Override
            public boolean handlesNull() {
                return false;
            }

            @Override
            public String map(Number sourceValue, AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualfier, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> context) {
                return sourceValue.toString();
            }

            @Override
            public boolean canHandle(AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) {
                return sourceType.equals(JavaBeanTypes.create(Number.class)) && targetType.equals(JavaBeanTypes.create(String.class));
            }
        };

        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withValueMapping(numberToString)
            .withObjectMapping(stringListMapping)
            .build ();

        // initialize the MappingDefProvider with the default List mapper
        mapper.map (Arrays.asList("a", "b"), List.class, String.class, null, List.class, String.class);

        final List<?> mapped = mapper.map (Arrays.asList (1, 2, 3), List.class, Integer.class, null, List.class, String.class);
        assertEquals (Arrays.asList("number 1", "number 2", "number 3"), mapped);

        final List<?> mappedAsObject = mapper.map (Arrays.asList (1, 2, 3), List.class, Number.class, null, List.class, String.class);
        assertEquals (Arrays.asList("1", "2", "3"), mappedAsObject);
    }
}
