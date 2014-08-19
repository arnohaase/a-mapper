package com.ajjpj.amapper.javabean;

import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.amapper.classes.ClassA;
import com.ajjpj.amapper.classes.ClassB;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.AObjectMappingDef;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;
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
                return types.sourceType().equals (JavaBeanTypes.create (StringHolder.class)) && types.targetType().equals (JavaBeanTypes.create (StringHolder.class));
            }

            @Override public StringHolder map(StringHolder source, StringHolder target, AQualifiedSourceAndTargetType types, AMapperWorker<?> worker, AMap<String, Object> context, APath path) throws Exception {
                return source.equals(target) ? target : source;
            }

            @Override public void diff(ADiffBuilder diff, StringHolder sourceOld, StringHolder sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
            }
        };

        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withObjectMapping(StringHolderAsObject)
                .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
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
            .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
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

    @Test
    public void testListFromArrayByIdentifier() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
            .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassA.class))
            .build();

        final ClassA[] source1 = new ClassA[] {new ClassA(), new ClassA()};

        // list of two different instances --> mapped to two separate instances
        @SuppressWarnings("unchecked")
        final List<ClassA> target1 = mapper.map (
                source1, ClassA[].class, ClassA.class,
                null,    List.class,  ClassA.class);
        assertEquals (2, target1.size());
        assertTrue (target1.get (0).getClass () == ClassA.class);
        assertTrue (target1.get(1).getClass() == ClassA.class);

        assertNotSame (target1.get (0), target1.get (1));
    }

    @Test
    public void testListToArrayByIdentifier() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withObjectMapping (BuiltinCollectionMappingDefs.ArrayFromCollectionMapping)
                .withBeanMapping (JavaBeanMapping.create (ClassA.class, ClassA.class))
                .build ();

        final List<ClassA> source1 = Arrays.asList(new ClassA(), new ClassA());

        final ClassA[] target1 = mapper.map (
                source1, List.class, ClassA.class,
                null,    ClassA[].class, ClassA.class);
        assertEquals (2, target1.length);
        assertTrue (target1[0].getClass () == ClassA.class);
        assertTrue (target1[1].getClass() == ClassA.class);

        assertNotSame (target1[0], target1[1]);
    }

    @Test
    public void testListToArrayOfSameSizeByIdentifier() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withObjectMapping (BuiltinCollectionMappingDefs.ArrayFromCollectionMapping)
                .withBeanMapping (JavaBeanMapping.create (ClassA.class, ClassA.class))
                .build();

        final List<ClassA> source1 = Arrays.asList(new ClassA(), new ClassA());

        final ClassA[] prevTarget = new ClassA[2];

        final ClassA[] target1 = mapper.map (
                source1,    List.class, ClassA.class,
                prevTarget, ClassA[].class, ClassA.class);
        assertSame (prevTarget, target1);
        assertEquals (2, target1.length);
        assertTrue (target1[0].getClass () == ClassA.class);
        assertTrue (target1[1].getClass () == ClassA.class);

        assertNotSame (target1[0], target1[1]);
    }

    @Test
    public void testPrimitiveCollectionToArray() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withObjectMapping (BuiltinCollectionMappingDefs.ArrayFromCollectionMapping)
                .build();

        final boolean[] mapped = mapper.map (
                Arrays.asList (true, false, false, true), List.class, Boolean.class,
                null, boolean[].class, boolean.class);

        assertEquals (boolean.class, mapped.getClass ().getComponentType ());
        assertEquals (4, mapped.length);
        assertEquals (true,  mapped[0]);
        assertEquals (false, mapped[1]);
        assertEquals (false, mapped[2]);
        assertEquals (true,  mapped[3]);
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testPrimitiveArrayToCollection() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withObjectMapping (BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
                .build();

        final List<Boolean> mapped = mapper.map (
                new boolean[] {true, false}, boolean[].class, boolean.class,
                null, List.class, Boolean.class);

        assertEquals (2, mapped.size());
        assertEquals (true,  mapped.get(0));
        assertEquals (false, mapped.get(1));
    }

    @Test
    public void testPrimitiveArrayToWrappedArray() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withObjectMapping (BuiltinCollectionMappingDefs.ArrayFromCollectionMapping)
                .build();

        final Boolean[] mapped = mapper.map (
                new boolean[] {true, false, false, true}, boolean[].class, boolean.class,
                null, Boolean[].class, Boolean.class);

        assertEquals (Boolean.class, mapped.getClass ().getComponentType ());
        assertEquals (4, mapped.length);
        assertEquals (true,  mapped[0]);
        assertEquals (false, mapped[1]);
        assertEquals (false, mapped[2]);
        assertEquals (true,  mapped[3]);
    }

    @Test
    public void testPrimitiveArrayFromWrappedArray() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withObjectMapping (BuiltinCollectionMappingDefs.ArrayFromCollectionMapping)
                .build();

        final boolean[] mapped = mapper.map (
                new Boolean[] {true, false, false, true}, Boolean[].class, Boolean.class,
                null, boolean[].class, boolean.class);

        assertEquals (boolean.class, mapped.getClass ().getComponentType ());
        assertEquals (4, mapped.length);
        assertEquals (true,  mapped[0]);
        assertEquals (false, mapped[1]);
        assertEquals (false, mapped[2]);
        assertEquals (true,  mapped[3]);
    }

    @Test
    public void testCollectionToArrayWithTransformation() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withObjectMapping (BuiltinCollectionMappingDefs.ArrayFromCollectionMapping)
                .withBeanMapping (JavaBeanMapping.create (ClassA.class, ClassB.class).addMapping ("firstName", "firstName"))
                .build ();

        final ClassA a = new ClassA();
        a.setFirstName ("First");

        final ClassB[] mapped = mapper.map (new ClassA[] {a}, new ClassB[0]);

        assertEquals (ClassB.class, mapped.getClass ().getComponentType ());
        assertEquals (1, mapped.length);
        assertEquals ("First", mapped[0].getFirstName ());
    }

    @Test
    public void testCollectionToArrayWithValueTransformation() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withObjectMapping (BuiltinCollectionMappingDefs.ArrayFromCollectionMapping)
                .build();

        final int[] mapped = mapper.map (
                new long[] {1, 2, 3, 4}, long[].class, long.class,
                null, int[].class, int.class);

        assertEquals (4, mapped.length);
        assertEquals (1,  mapped[0]);
        assertEquals (2, mapped[1]);
        assertEquals (3, mapped[2]);
        assertEquals (4, mapped[3]);
    }
}
