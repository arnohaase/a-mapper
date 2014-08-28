package com.ajjpj.amapper.core;

import com.ajjpj.amapper.classes.SourceParentWithId;
import com.ajjpj.amapper.classes.TargetParentWithId;
import com.ajjpj.amapper.collection.LevenshteinBasedListMappingDef;
import com.ajjpj.amapper.core.tpe.AQualifiedType;
import com.ajjpj.amapper.javabean.JavaBeanMapper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Roman
 */
public class LevenshteinBasedListMappingDefTest {

    final AIdentifierExtractor ie = new AIdentifierExtractor() {
        @Override public Object uniqueIdentifier(Object o, AQualifiedType type, AQualifiedType targetType) {
            if (o instanceof SourceParentWithId) {
                return ((SourceParentWithId)o).getSourceId();
            }
            if (o instanceof TargetParentWithId) {
                return ((TargetParentWithId)o).getTargetId();
            }
            if (o instanceof Collection) {
                return ""; // all collections are 'equal' as and of themselves
            }
            throw new IllegalArgumentException(String.valueOf(o));
        }
    };

    final JavaBeanMapper mapper;

    {
        try {
            mapper = JavaBeanMapperBuilder.create ()
                    .withIdentifierExtractor (ie)
                    .withObjectMapping (BuiltinCollectionMappingDefs.LevenshteinListByIdentifierMapping)
                    .withBeanMapping (JavaBeanMapping.create (SourceParentWithId.class, TargetParentWithId.class)
                                    .addMapping ("sourceId", "targetId")
                                    .addMapping ("sourceAttrib", "targetAttrib")
                    )
                    .build ();
        } catch (Exception e) {
            throw new RuntimeException ();
        }
    }

    @Test public void testMapSourceNull() throws Exception {
        final List<TargetParentWithId> target = Arrays.asList (
                new TargetParentWithId (0, "elem0"),
                new TargetParentWithId (1, "elem1")
        );
        final List<TargetParentWithId> mapResult = mapper.mapList (null, SourceParentWithId.class, target, TargetParentWithId.class);

        assertNull (mapResult);
    }

    @Test public void testMapTargetNull() throws Exception {
        final List<SourceParentWithId> source = Arrays.asList (
                new SourceParentWithId (0, "elem0"),
                new SourceParentWithId (1, "elem1")
        );
        final List<TargetParentWithId> expectedResult = Arrays.asList (
                new TargetParentWithId (0, "elem0"),
                new TargetParentWithId (1, "elem1")
        );
        final List<TargetParentWithId> mapResult = mapper.mapList (source, SourceParentWithId.class, null, TargetParentWithId.class);

        assertEquals (expectedResult.size(), mapResult.size());
        assertArrayEquals (expectedResult.toArray (), mapResult.toArray ());
    }

    @Test public void testMapSourceAndTargetNull() throws Exception {
        final List<TargetParentWithId> mapResult = mapper.mapList (null, SourceParentWithId.class, null, TargetParentWithId.class);

        assertNull (mapResult);
    }


    @Test public void testMapSourceEmpty() throws Exception {
        final List<SourceParentWithId> source = new ArrayList<>();
        final List<TargetParentWithId> target = new ArrayList<>(Arrays.asList (
                new TargetParentWithId (0, "elem0"),
                new TargetParentWithId (1, "elem1")
        ));
        final List<TargetParentWithId> mapResult = mapper.mapList (source, SourceParentWithId.class, target, TargetParentWithId.class);

        assertEquals (0, target.size());
    }

    @Test public void testMapEmptyTarget() throws Exception {
        final List<SourceParentWithId> source = Arrays.asList (
                new SourceParentWithId (0, "elem0"),
                new SourceParentWithId (1, "elem1")
        );
        final List<TargetParentWithId> target = new ArrayList<>();
        final List<TargetParentWithId> expectedResult = Arrays.asList (
                new TargetParentWithId (0, "elem0"),
                new TargetParentWithId (1, "elem1")
        );
        final List<TargetParentWithId> mapResult = mapper.mapList (source, SourceParentWithId.class, target, TargetParentWithId.class);

        assertEquals (expectedResult.size(), mapResult.size());
        assertArrayEquals (expectedResult.toArray(), mapResult.toArray ());
    }

    @Test public void testMapSourceAndTargetEmpty() throws Exception {
        final List<SourceParentWithId> source = new ArrayList<> ();
        final List<TargetParentWithId> target = new ArrayList<> ();
        final List<TargetParentWithId> mapResult = mapper.mapList (source, SourceParentWithId.class, target, TargetParentWithId.class);

        assertEquals (0, mapResult.size ());
    }

    @Test public void testMapIdenticalElements() throws Exception {
        final List<SourceParentWithId> source = Arrays.asList (
                new SourceParentWithId (0, "elem0"),
                new SourceParentWithId (1, "elemX"),
                new SourceParentWithId (0, "elem0"),
                new SourceParentWithId (0, "elem0"),
                new SourceParentWithId (1, "elem1")
        );
        final List<TargetParentWithId> target = new ArrayList<>( Arrays.asList (
                new TargetParentWithId (1, "elem1"),
                new TargetParentWithId (1, "elem1")
        ));
        final List<TargetParentWithId> expectedResult = Arrays.asList (
                new TargetParentWithId (0, "elem0"),
                new TargetParentWithId (1, "elemX"),
                new TargetParentWithId (0, "elem0"),
                new TargetParentWithId (0, "elem0"),
                new TargetParentWithId (1, "elem1")
        );
        final List<TargetParentWithId> mapResult = mapper.mapList (source, SourceParentWithId.class, target, TargetParentWithId.class);

        assertArrayEquals (expectedResult.toArray(), mapResult.toArray());
    }

    @Test public void testShotgun() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create ()
                .withObjectMapping (new LevenshteinBasedListMappingDef ())
                .build ();

        final Random random = new Random (12345);

        final List<Integer> l1 = new ArrayList<> ();
        for (int i=0; i<2000; i++) {
            l1.add (random.nextInt (10));
        }

        final List<Integer> l2 = new ArrayList<> ();
        for (int i=0; i<2000; i++) {
            l2.add (random.nextInt (10));
        }

        final long start = System.currentTimeMillis ();
        mapper.mapList (l1, Integer.class, l2, Integer.class);
        System.out.println ((System.currentTimeMillis () - start) + "ms");

        assertEquals (l1, l2);
    }
}
