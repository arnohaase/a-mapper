package com.ajjpj.amapper.core;

import com.ajjpj.abase.collection.immutable.AList;
import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.amapper.classes.*;
import com.ajjpj.amapper.core.diff.ADiff;
import com.ajjpj.amapper.core.diff.ADiffElement;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.path.APathSegment;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.JavaBeanMapper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author arno
 */
public class DiffTest {
    @Test
    public void testSimpleDiff() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withBeanMapping(JavaBeanMapping.create(ClassA.class, ClassB.class)
                        .addMapping("firstName", String.class, "firstName", String.class)
                        .addMapping("lastName", String.class, "lastName", String.class))
                .build();

        final ClassA  a1 = new ClassA();
        final ClassA a2 = new ClassA();

        a1.setFirstName("Arno");
        a1.setLastName("Haase");

        a2.setFirstName("Fred");
        a2.setLastName("Haase");

        final ADiff diff = mapper.diff(a1, a2, ClassA.class, ClassB.class);
        assertEquals (2, diff.getElements().size());
        assertTrue(diff.getPaths().contains(APath.EMPTY));
        assertTrue (diff.getPaths().contains(APath.fromSegments(APathSegment.simple("firstName"))));

        final AOption<ADiffElement> firstNameDiff = diff.getSingle(APath.fromSegments(APathSegment.simple("firstName")));
        assertEquals(true, firstNameDiff.isDefined());
        assertEquals("Arno", firstNameDiff.get().oldValue);
        assertEquals("Fred", firstNameDiff.get().newValue);
        assertEquals(true, firstNameDiff.get().isDerived);

        assertEquals(firstNameDiff, diff.getSingle("firstName"));
    }

    //TODO ref with same identifier

    final AIdentifierExtractor ie = new AIdentifierExtractor() {
        @Override public Object uniqueIdentifier(Object o, AQualifiedSourceAndTargetType types) {
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
            if(o instanceof Collection) {
                return ""; // all collections are 'equal' as and of themselves
            }
            throw new IllegalArgumentException(String.valueOf(o));
        }
    };

    final JavaBeanMapper mapper;

    {
        try {
            mapper = JavaBeanMapperBuilder.create()
                    .withIdentifierExtractor(ie)
                    .withBeanMapping(JavaBeanMapping.create(DiffSource.class, DiffTarget.class)
                            .addMapping("oid", String.class, "oid", Long.class)
                            .addMapping("sourceName", String.class, "targetName", String.class)
                            .addMapping("sourceChild", DiffSourceChild.class, "targetChild", DiffTargetChild.class)
                    )
                    .withBeanMapping(JavaBeanMapping.create(DiffSourceChild.class, DiffTargetChild.class)
                            .addMapping("oid", String.class, "oid", Long.class)
                            .addMapping("sourceNum", Double.class, "targetNum", Integer.class)
                    )
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDiffEqual() throws Exception {
        final DiffSource s1 = new DiffSource(1, "source", new DiffSourceChild(2, 123.0));
        final DiffSource s2 = new DiffSource(1, "source", new DiffSourceChild(2, 123.0));

        final ADiff diff = mapper.diff(s1, s2, DiffSource.class, DiffTarget.class);
        assertTrue (diff.isEmpty());
    }

    @Test
    public void testDiffSourceDifferentButTargetEqual() throws Exception {
        final DiffSource s1 = new DiffSource(1, "source", new DiffSourceChild(2, 123.1));
        final DiffSource s2 = new DiffSource(1, "source", new DiffSourceChild(2, 123.2));

        final ADiff diff = mapper.diff(s1, s2, DiffSource.class, DiffTarget.class);
        assertTrue (diff.isEmpty());
    }

    @Test
    public void testDiffAttributeDifferent() throws Exception {
        final DiffSource s1 = new DiffSource(1, "source", new DiffSourceChild(2, 123.1));
        final DiffSource s2 = new DiffSource(1, "source", new DiffSourceChild(2, 124.1));

        final ADiff diff = mapper.diff(s1, s2, DiffSource.class, DiffTarget.class);
        assertEquals(1, diff.getElements().size());

        assertEquals(Integer.valueOf(123),        diff.getSingle("targetChild.targetNum").get().oldValue);
        assertEquals(Integer.valueOf(124),        diff.getSingle("targetChild.targetNum").get().newValue);
        assertEquals(false,                       diff.getSingle("targetChild.targetNum").get().isDerived);
        assertEquals(ADiffElement.Kind.Attribute, diff.getSingle("targetChild.targetNum").get().kind);
    }

    @Test
    public void testRefDifferent() throws Exception {
        final DiffSource s1 = new DiffSource(1, "source", new DiffSourceChild(2, 123.1));
        final DiffSource s2 = new DiffSource(1, "source", new DiffSourceChild(3, 123.1));

        final ADiff diff = mapper.diff(s1, s2, DiffSource.class, DiffTarget.class);
        assertEquals(2, diff.getElements().size());

        assertEquals(2L,                          diff.getSingle("targetChild").get().oldValue);
        assertEquals(3L,                          diff.getSingle("targetChild").get().newValue);
        assertEquals(false,                       diff.getSingle("targetChild").get().isDerived);
        assertEquals(ADiffElement.Kind.RefChange, diff.getSingle("targetChild").get().kind);

        assertEquals(2L,                          diff.getSingle("targetChild.oid").get().oldValue);
        assertEquals(3L,                          diff.getSingle("targetChild.oid").get().newValue);
        assertEquals(true,                        diff.getSingle("targetChild.oid").get().isDerived);
        assertEquals(ADiffElement.Kind.Attribute, diff.getSingle("targetChild.oid").get().kind);
    }

    @Test
    public void testCascade() throws Exception {
        final DiffSource s1 = new DiffSource(1, "source1", new DiffSourceChild(3, 123.1));
        final DiffSource s2 = new DiffSource(2, "source2", new DiffSourceChild(4, 123.1));

        final ADiff diff = mapper.diff(s1, s2, DiffSource.class, DiffTarget.class);
        assertEquals(5, diff.getElements().size());

        assertEquals(1L,        diff.getSingle("").get().oldValue);
        assertEquals(2L,        diff.getSingle("").get().newValue);
        assertEquals(false,     diff.getSingle("").get().isDerived);

        assertEquals(1L,        diff.getSingle("oid").get().oldValue);
        assertEquals(2L,        diff.getSingle("oid").get().newValue);
        assertEquals(true,      diff.getSingle("oid").get().isDerived);

        assertEquals("source1", diff.getSingle("targetName").get().oldValue);
        assertEquals("source2", diff.getSingle("targetName").get().newValue);
        assertEquals(true,      diff.getSingle("targetName").get().isDerived);

        assertEquals(3L,        diff.getSingle("targetChild").get().oldValue);
        assertEquals(4L,        diff.getSingle("targetChild").get().newValue);
        assertEquals(true,      diff.getSingle("targetChild").get().isDerived);

        assertEquals(3L,        diff.getSingle("targetChild.oid").get().oldValue);
        assertEquals(4L,        diff.getSingle("targetChild.oid").get().newValue);
        assertEquals(true,      diff.getSingle("targetChild.oid").get().isDerived);
    }

    @Test
    public void testFromCascade() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withIdentifierExtractor(ie)
                .withBeanMapping(JavaBeanMapping.create(DiffSource.class, DiffTarget.class)
                        .addMapping("sourceChild.sourceNum", Double.class, "derivedTargetNum", Integer.class)
                )
                .build();

        final DiffSource s1 = new DiffSource(1, "", new DiffSourceChild(1, 1.0));
        final DiffSource s2 = new DiffSource(1, "", new DiffSourceChild(1, 2.0));

        final ADiff diff = mapper.diff(s1, s2, DiffSource.class, DiffTarget.class);
        assertEquals(1, diff.getElements().size());

        assertEquals(1,     diff.getSingle("derivedTargetNum").get().oldValue);
        assertEquals(2,     diff.getSingle("derivedTargetNum").get().newValue);
        assertEquals(false, diff.getSingle("derivedTargetNum").get().isDerived);
    }

    @Test
    public void testToCascade() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withIdentifierExtractor(ie)
                .withBeanMapping(JavaBeanMapping.create(DiffSource.class, DiffTarget.class)
                        .addMapping("sourceChild.sourceNum", Double.class, "derivedTargetNum", Integer.class)
                )
                .build();

        final DiffTarget s1 = new DiffTarget(1, "", new DiffTargetChild(1, 1));
        final DiffTarget s2 = new DiffTarget(1, "", new DiffTargetChild(1, 2));

        s1.setDerivedTargetNum(1);
        s2.setDerivedTargetNum(2);

        final ADiff diff = mapper.diff(s1, s2, DiffTarget.class, DiffSource.class);
        assertEquals(1, diff.getElements().size());

        assertEquals(1.0,   diff.getSingle("sourceChild.sourceNum").get().oldValue);
        assertEquals(2.0,   diff.getSingle("sourceChild.sourceNum").get().newValue);
        assertEquals(false, diff.getSingle("sourceChild.sourceNum").get().isDerived);

        final AList<APathSegment> segments = diff.getSingle("sourceChild.sourceNum").get().path.getSegments();
        assertEquals(1, segments.size());
        assertEquals("sourceChild.sourceNum", segments.head().getName());
    }

    @Test
    public void testDiffOgnl() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withIdentifierExtractor(ie)
                .withBeanMapping(JavaBeanMapping.create(DiffSource.class, DiffTarget.class)
                        .addOneWayMapping("sourceChild.getSourceNum()", Double.class, "derivedTargetNum", Integer.class)
                )
                .build();

        final DiffSource s1 = new DiffSource(1, "", new DiffSourceChild(1, 1.0));
        final DiffSource s2 = new DiffSource(1, "", new DiffSourceChild(1, 2.0));

        final ADiff diff = mapper.diff(s1, s2, DiffSource.class, DiffTarget.class);
        assertEquals(1, diff.getElements().size());

        assertEquals(1,     diff.getSingle("derivedTargetNum").get().oldValue);
        assertEquals(2,     diff.getSingle("derivedTargetNum").get().newValue);
        assertEquals(false, diff.getSingle("derivedTargetNum").get().isDerived);
    }

    private void checkDiffElement(ADiffElement el, ADiffElement.Kind kind, Object oldValue, Object newValue, boolean isDerived) {
        assertEquals(kind, el.kind);
        assertEquals(oldValue, el.oldValue);
        assertEquals(newValue, el.newValue);
        assertEquals(isDerived, el.isDerived);
    }

    @Test
    public void testMergeList() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withIdentifierExtractor(ie)
                .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(DiffSource.class, DiffTarget.class)
                        .addMapping("sourceChildren", List.class, DiffSourceChild.class, "targetChildren", List.class, DiffTargetChild.class)
                )
                .withBeanMapping(JavaBeanMapping.create(DiffSourceChild.class, DiffTargetChild.class)
                        .addMapping("oid", String.class, "oid", Long.class)
                        .addMapping("sourceNum", Double.class, "targetNum", Integer.class)
                )
                .build();

        final DiffSource s1 = new DiffSource(1, "", null);
        final DiffSource s2 = new DiffSource(1, "", null);

        s1.getSourceChildren().add(new DiffSourceChild(1, 1.0));
        s1.getSourceChildren().add(new DiffSourceChild(2, 2.0));
        s1.getSourceChildren().add(new DiffSourceChild(3, 3.0));

        s2.getSourceChildren().add(new DiffSourceChild(1, 1.0));
        s2.getSourceChildren().add(new DiffSourceChild(2, 20.0));
        s2.getSourceChildren().add(new DiffSourceChild(4, 4.0));

        final ADiff diff = mapper.diff(s1, s2, DiffSource.class, DiffTarget.class);
        assertEquals (7, diff.getElements().size());

        final APathSegment childrenSeg = APathSegment.simple("targetChildren");
        final APathSegment elSeg2 = APathSegment.parameterized("elements", 2L);
        final APathSegment elSeg3 = APathSegment.parameterized("elements", 3L);
        final APathSegment elSeg4 = APathSegment.parameterized("elements", 4L);

        checkDiffElement(diff.byPath.getRequired(APath.fromSegments (childrenSeg, elSeg2, APathSegment.simple("targetNum"))), ADiffElement.Kind.Attribute, 2,   20,    false);
        checkDiffElement(diff.byPath.getRequired(APath.fromSegments (childrenSeg, elSeg3)),                                   ADiffElement.Kind.Remove,    3L,   null, false);
        checkDiffElement(diff.byPath.getRequired(APath.fromSegments (childrenSeg, elSeg3, APathSegment.simple("oid"))),       ADiffElement.Kind.Attribute, 3L,   null, true);
        checkDiffElement(diff.byPath.getRequired(APath.fromSegments (childrenSeg, elSeg3, APathSegment.simple("targetNum"))), ADiffElement.Kind.Attribute, 3,    null, true);
        checkDiffElement(diff.byPath.getRequired(APath.fromSegments (childrenSeg, elSeg4)),                                   ADiffElement.Kind.Add,       null, 4L,   false);
        checkDiffElement(diff.byPath.getRequired(APath.fromSegments (childrenSeg, elSeg4, APathSegment.simple("oid"))),       ADiffElement.Kind.Attribute, null, 4L,   true);
        checkDiffElement(diff.byPath.getRequired(APath.fromSegments (childrenSeg, elSeg4, APathSegment.simple("targetNum"))), ADiffElement.Kind.Attribute, null, 4,    true);

        assertEquals(new HashSet<String>(Arrays.asList("targetChildren.elements", "targetChildren.elements.oid", "targetChildren.elements.targetNum")), diff.pathStrings.asJavaUtilSet());
        assertEquals(2, diff.byPathString.getRequired("targetChildren.elements").          size());
        assertEquals(2, diff.byPathString.getRequired("targetChildren.elements.oid").      size());
        assertEquals(3, diff.byPathString.getRequired("targetChildren.elements.targetNum").size());
    }

    @Test
    public void testDeferred() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withIdentifierExtractor(ie)
                .withObjectMapping(BuiltinCollectionMappingDefs.ListWithoutDuplicatesByIdentifierMapping)
                .withBeanMapping(JavaBeanMapping.create(DiffSource.class, DiffTarget.class)
                        .addMapping("sourceName", String.class, "targetName", String.class)
//                        .addMapping("sourceChildren", List.class, DiffSourceChild.class, "targetChildren", List.class, DiffTargetChild.class)
                )
                .withBeanMapping(JavaBeanMapping.create(DiffSourceChild.class, DiffTargetChild.class)
                        .addMapping("oid", String.class, "oid", Long.class)
                        .addMapping("sourceNum", Double.class, "targetNum", Integer.class)
                        .addMapping("sourceParent", DiffSource.class, "targetParent", DiffTarget.class)
                )
                .build();

        final DiffSource s11 = new DiffSource(1, "",    new DiffSourceChild(3, 3));
        final DiffSource s12 = new DiffSource(2, "old", null);
        s11.getSourceChild().setSourceParent(s12);

        final DiffSource s21 = new DiffSource(1, "",    new DiffSourceChild(3, 3));
        final DiffSource s22 = new DiffSource(2, "new", null);
        s21.getSourceChild().setSourceParent(s22);

        final List<DiffSource> list1 = Arrays.asList(s11, s12);
        final List<DiffSource> list2 = Arrays.asList(s21, s22);

        final ADiff diff = mapper.diffList(list1, list2, DiffSource.class, DiffTarget.class);

        assertEquals(1, diff.getElements().size());
        checkDiffElement(diff.getSingle("elements.targetName").get(), ADiffElement.Kind.Attribute, "old", "new", false);
    }
}





































