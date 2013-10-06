package com.ajjpj.amapper.core2;

import com.ajjpj.amapper.classes.ClassCyclicChild;
import com.ajjpj.amapper.classes.ClassCyclicParent;
import com.ajjpj.amapper.core2.AMapperDiffWorker;
import com.ajjpj.amapper.core2.AMapperWorker;
import com.ajjpj.amapper.core2.AObjectMappingDef;
import com.ajjpj.amapper.core2.AValueMappingDef;
import com.ajjpj.amapper.core2.diff.ADiffBuilder;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean2.JavaBeanMapper;
import com.ajjpj.amapper.javabean2.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean2.JavaBeanTypes;
import com.ajjpj.amapper.javabean2.SingleParamBeanType;
import com.ajjpj.amapper.javabean2.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean2.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean2.mappingdef.BuiltinCollectionMappingDefs;
import com.ajjpj.amapper.util.coll.AMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


public class CoreTest {
    @Test
    public void testCyclicRef () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassCyclicParent.class, ClassCyclicParent.class).withMatchingPropsMappings())
            .withBeanMapping (JavaBeanMapping.create(ClassCyclicChild.class, ClassCyclicChild.class).withMatchingPropsMappings())
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
            .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
            .withBeanMapping (JavaBeanMapping.create (ClassCyclicParent.class, ClassCyclicParent.class).withMatchingPropsMappings())
            .withBeanMapping (JavaBeanMapping.create (ClassCyclicChild.class, ClassCyclicChild.class).withMatchingPropsMappings())
            .build ();

        final ClassCyclicParent mappedParent = mapper.map (parent, ClassCyclicParent.class);

        assertNotSame (parent, mappedParent);
        assertSame (mappedParent, mappedParent.getChildList().get(0).getParent());
    }

    @Test
    public void testObjectMappingPerElementClass () throws Exception {
        final AObjectMappingDef<List<?>, List<?>, JavaBeanMappingHelper> stringListMapping = new AObjectMappingDef<List<?>, List<?>, JavaBeanMappingHelper> () {
            @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
                if(types.sourceType instanceof SingleParamBeanType && types.targetType instanceof SingleParamBeanType) {
                    final SingleParamBeanType sourceType = (SingleParamBeanType) types.sourceType;
                    final SingleParamBeanType targetType = (SingleParamBeanType) types.targetType;

                    return sourceType.cls == List.class && sourceType.paramCls == Integer.class && targetType.cls == List.class && targetType.paramCls == String.class;
                }

                return false;
            }

            @Override public boolean isCacheable() {
                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public List<?> map(List<?> source, List<?> target, AQualifiedSourceAndTargetType types, AMapperWorker<? extends JavaBeanMappingHelper> worker, AMap<String, Object> context, APath path) {
                final LinkedList<String> result = new LinkedList<String>();
                for (Integer i: (Collection<Integer>) source) {
                    result.add ("number " + i);
                }
                return result;
            }

            @Override public void diff(ADiffBuilder diff, List<?> sourceOld, List<?> sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends JavaBeanMappingHelper> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
            }
        };

        final AValueMappingDef<Number, String, JavaBeanMappingHelper> numberToString = new AValueMappingDef<Number, String, JavaBeanMappingHelper>() {
            @Override public String map(Number sourceValue, AQualifiedSourceAndTargetType types, AMapperWorker<? extends JavaBeanMappingHelper> worker, AMap<String, Object> context) {
                return sourceValue == null ? null : sourceValue.toString();
            }

            @Override public void diff(ADiffBuilder diff, Number sourceOld, Number sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends JavaBeanMappingHelper> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
            }

            @Override
            public boolean canHandle(AQualifiedSourceAndTargetType types) {
                return types.sourceType.equals(JavaBeanTypes.create(Number.class)) && types.targetType.equals(JavaBeanTypes.create(String.class));
            }
        };

        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
            .withValueMapping(numberToString)
            .withObjectMapping(stringListMapping)
            .build ();

        // initialize the mappingDefProvider with the default List mapper
        mapper.map(Arrays.asList("a", "b"), List.class, String.class, null, List.class, String.class);

        final List<?> AMapped = mapper.map (Arrays.asList (1, 2, 3), List.class, Integer.class, null, List.class, String.class);
        assertEquals (Arrays.asList("number 1", "number 2", "number 3"), AMapped);

        final List<?> mappedAsObject = mapper.map (Arrays.asList (1, 2, 3), List.class, Number.class, null, List.class, String.class);
        assertEquals (Arrays.asList("1", "2", "3"), mappedAsObject);
    }
}
