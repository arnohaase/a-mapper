package com.ajjpj.amapper.javabean2;

import com.ajjpj.amapper.classes.ClassA;
import com.ajjpj.amapper.core2.AMapperDiffWorker;
import com.ajjpj.amapper.core2.AMapperWorker;
import com.ajjpj.amapper.core2.AObjectMappingDef;
import com.ajjpj.amapper.core2.diff.ADiffBuilder;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean2.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean2.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean2.mappingdef.BuiltinCollectionMappingDefs;
import com.ajjpj.amapper.util.coll.AMap;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


public class CollectionTest {
    @Test
    public void testSet() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withObjectMapping(BuiltinCollectionMappingDefs.SetByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassA.class))
            .build();

        final Set<ClassA> set = new HashSet<ClassA>();
        final ClassA a1 = new ClassA();
        a1.setFirstName("A");
        set.add(a1);
        final ClassA a2 = new ClassA();
        a2.setFirstName("B");
        set.add(a2);

        @SuppressWarnings("unchecked")
        final Set<ClassA> mapped = mapper.map(set, Set.class, ClassA.class, null, Set.class, ClassA.class);

        assertEquals(2, mapped.size());
    }

    public static class StringHolder {
        private final String s;

        public StringHolder(String s) {
            this.s = s;
        }

        public String getS() {
            return s;
        }

        @Override public String toString() {
            return "{" + s + "}";
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StringHolder that = (StringHolder) o;

            if (s != null ? !s.equals(that.s) : that.s != null) return false;

            return true;
        }

        @Override public int hashCode() {
            return s != null ? s.hashCode() : 0;
        }
    }

    @Test
    public void testListByIdentifierIsMerged() throws Exception {
        final AObjectMappingDef<StringHolder,StringHolder,Object> StringHolderAsObject = new AObjectMappingDef<StringHolder, StringHolder, Object>() {
            @Override  public boolean isCacheable() {
                return true;
            }

            @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
                return types.sourceType.equals(JavaBeanTypes.create(StringHolder.class)) && types.targetType.equals(JavaBeanTypes.create(StringHolder.class));
            }

            @Override public StringHolder map(StringHolder source, StringHolder target, AQualifiedSourceAndTargetType types, AMapperWorker<?> worker, AMap<String, Object> context, APath path) throws Exception {
                return source.equals(target) ? target : source;
            }

            @Override public void diff(ADiffBuilder diff, StringHolder sourceOld, StringHolder sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
            }
        };

        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(StringHolderAsObject)
                .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
                .build();

        // Verify that the string's identity is preserved. In a real-world application, this is weird behavior for
        //  dealing with strings, but this illustrates the point for dealing with 'real' objects.

        final StringHolder a = new StringHolder("a");
        final List<StringHolder> target = new ArrayList<StringHolder>();
        target.add(new StringHolder("b"));
        target.add(a);
        target.add(new StringHolder("c"));

        // map ["a", "b", "d"] into ["b", "a", "c"]
        final List<StringHolder> mapped = mapper.mapList(Arrays.asList(new StringHolder("a"), new StringHolder("b"), new StringHolder("d")), StringHolder.class, target, StringHolder.class);

        assertSame(target, mapped);
        assertEquals(3, target.size());
        assertTrue(target.contains(new StringHolder("a")));
        assertTrue(target.contains(new StringHolder("b")));
        assertTrue(target.contains(new StringHolder("d")));

        // check that the 'a' instance in the target list was not replaced
        final StringHolder aFromResult = target.get(target.indexOf(new StringHolder("a")));
        assertSame(a, aFromResult);
    }

    @Test
    public void testListByIdentifier() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withObjectMapping(BuiltinCollectionMappingDefs.ListByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassA.class))
            .build();

        final List<ClassA> source1 = Arrays.asList(new ClassA(), new ClassA());

        // list of two different instances --> mapped to two separate instances
        @SuppressWarnings("unchecked")
        final List<ClassA> target1 = mapper.map (
                source1, List.class, ClassA.class,
                null,    List.class, ClassA.class);
        assertEquals (2, target1.size());
        assertTrue (target1.get(0).getClass() == ClassA.class);
        assertTrue (target1.get(1).getClass() == ClassA.class);

        assertNotSame(target1.get(0), target1.get(1));
    }
}
