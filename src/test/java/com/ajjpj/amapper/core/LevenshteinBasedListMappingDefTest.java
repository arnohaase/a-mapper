package com.ajjpj.amapper.core;

import com.ajjpj.amapper.classes.DiffSource;
import com.ajjpj.amapper.classes.DiffSourceChild;
import com.ajjpj.amapper.classes.DiffTarget;
import com.ajjpj.amapper.classes.DiffTargetChild;
import com.ajjpj.amapper.core.diff.ADiff;
import com.ajjpj.amapper.core.tpe.AQualifiedType;
import com.ajjpj.amapper.javabean.JavaBeanMapper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Roman
 */
public class LevenshteinBasedListMappingDefTest {

    final AIdentifierExtractor ie = new AIdentifierExtractor() {
        @Override public Object uniqueIdentifier(Object o, AQualifiedType type, AQualifiedType targetType) {
            if(o instanceof DiffSource) {
                return ((DiffSource)o).getOid();
            }
            if(o instanceof DiffTarget) {
                return ((DiffTarget)o).getOid();
            }
            if(o instanceof DiffSourceChild) {
                return ((DiffSourceChild)o).getOid();
            }
            if(o instanceof DiffTargetChild) {
                return ((DiffTargetChild)o).getOid();
            }
            if(o instanceof Collection ) {
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
                    .withBeanMapping (JavaBeanMapping.create (DiffSource.class, DiffTarget.class).withMatchingPropsMappings ()
                                    .addMapping ("sourceName", "targetName")
                                    .addMapping ("sourceChildren", "targetChildren")
                    )
                    .withBeanMapping (JavaBeanMapping.create (DiffSourceChild.class, DiffTargetChild.class).withMatchingPropsMappings ())
                    .build ();
        } catch (Exception e) {
            throw new RuntimeException ();
        }
    }

    @Test public void testMapSourceAndTargetNull() throws Exception {
        final List<DiffTarget> mapResult = mapper.mapList (null, DiffSource.class, null, DiffTarget.class);

        assertNull (mapResult);
    }

    @Test public void testMapSourceNull() throws Exception {
        final List<DiffTarget> target = Arrays.asList (
                new DiffTarget (0, "elem0", null),
                new DiffTarget (1, "elem1", null)
        );
        final List<DiffTarget> mapResult = mapper.mapList (null, DiffSource.class, target, DiffTarget.class);

        assertNull (mapResult);
    }

    // fails - what is expected?
    @Test public void testMapTargetNull() throws Exception {
        final List<DiffSource> source = Arrays.asList (
                new DiffSource (0, "elem0", null),
                new DiffSource (1, "elem1", null)
        );
        List<DiffTarget> target = null;
        final List<DiffTarget> expectedResult = Arrays.asList (
                new DiffTarget (0, "elem0", null),
                new DiffTarget (1, "elem1", null)
        );
        final List<DiffTarget> mapResult = mapper.mapList (source, DiffSource.class, target, DiffTarget.class);

        assertArrayEquals (expectedResult.toArray(), mapResult.toArray ());
    }

    // fails! why?
    @Test public void testMapTargetEmpty() throws Exception {
        final List<DiffSource> source = Arrays.asList (
                new DiffSource (0, "elem0", null),
                new DiffSource (1, "elem1", null)
        );
        List<DiffTarget> target = new ArrayList<>();
        final List<DiffTarget> expectedResult = Arrays.asList (
                new DiffTarget (0, "elem0", null),
                new DiffTarget (1, "elem1", null)
        );
        final List<DiffTarget> mapResult = mapper.mapList (source, DiffSource.class, target, DiffTarget.class);

        assertArrayEquals (expectedResult.toArray(), mapResult.toArray ());
    }

//    java.lang.IndexOutOfBoundsException: Index: 1, Size: 1
//    at java.util.ArrayList.rangeCheck(ArrayList.java:635)
//    at java.util.ArrayList.remove(ArrayList.java:474)
//    at com.ajjpj.amapper.collection.LevenshteinDistance.edit(LevenshteinDistance.java:176)
//    at com.ajjpj.amapper.collection.LevenshteinDistance.editTarget(LevenshteinDistance.java:76)
//    at com.ajjpj.amapper.collection.LevenshteinBasedListMappingDef.map(LevenshteinBasedListMappingDef.java:83)
//    at com.ajjpj.amapper.core.impl.AMapperWorkerImpl.mapObject(AMapperWorkerImpl.java:104)
    @Test public void testMapSourceEmpty() throws Exception {
        final List<DiffSource> source = new ArrayList<>();
        List<DiffTarget> target = new ArrayList<>(Arrays.asList (
                new DiffTarget (0, "elem0", null),
                new DiffTarget (1, "elem1", null)
        ));
        final List<DiffTarget> mapResult = mapper.mapList (source, DiffSource.class, target, DiffTarget.class);

        assertEquals (0, target.size());
    }


// fails: com.ajjpj.amapper.core.exclog.AMapperException: AMapper exception @APath{elements[0@0]}
//    Caused by: java.lang.InstantiationException: com.ajjpj.amapper.classes.DiffTarget
//    at java.lang.Class.newInstance(Class.java:359)
//    at com.ajjpj.amapper.javabean.SimpleJavaBeanMappingHelper.provideInstance(SimpleJavaBeanMappingHelper.java:18)
    @Test public void testMapIdenticalElements() throws Exception {
        final List<DiffSource> source = Arrays.asList (
                new DiffSource (0, "elem0", null),
                new DiffSource (1, "elemX", null),
                new DiffSource (0, "elem0", null),
                new DiffSource (0, "elem0", null),
                new DiffSource (1, "elem1", null));
        final List<DiffTarget> target = new ArrayList<>( Arrays.asList (
                new DiffTarget (1, "elem1", null),
                new DiffTarget (1, "elem1", null)
        ));
        final List<DiffTarget> expectedResult = Arrays.asList (
                new DiffTarget (0, "elem0", null),
                new DiffTarget (1, "elemX", null),
                new DiffTarget (0, "elem0", null),
                new DiffTarget (0, "elem0", null),
                new DiffTarget (1, "elem1", null));

        final List<DiffTarget> mapResult = mapper.mapList (source, DiffSource.class, target, DiffTarget.class);

        assertArrayEquals (expectedResult.toArray (), mapResult.toArray ());
    }

    // TODO multiple identical elements
//
//    final List<DiffSource> leftList = new ArrayList<> (Arrays.asList (
//            new DiffSource (0, "elem0", null),
//            new DiffSource (1, "elem1", null)
//    ));
//    leftList.get (1).setSourceChildre, new DiffSourceChild (1,1))));
//
//
//    final ADiff diff = mapper.diffList (leftList, rightList, DiffSource.class, DiffTarget.class);

}
