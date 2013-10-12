package com.ajjpj.amapper.core;

import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.path.APathSegment;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author arno
 */
public class PathTest {
    @Test
    public void testBuild() {
        final APath p = APath.EMPTY.withChild(APathSegment.simple("a")).withChild(APathSegment.simple("b"));
        assertEquals(APath.fromSegments(APathSegment.simple("a"), APathSegment.simple("b")), p);

        final APath p1 = p.withChild(APathSegment.simple("c1"));
        final APath p2 = p.withChild(APathSegment.simple("c2"));

        assertEquals(APath.fromSegments(APathSegment.simple("a"), APathSegment.simple("b"), APathSegment.simple("c1")), p1);
        assertEquals(APath.fromSegments(APathSegment.simple("a"), APathSegment.simple("b"), APathSegment.simple("c2")), p2);
    }

    @Test
    public void testToString() {
        assertEquals("APath{a.b[keyB]}", APath.fromSegments(APathSegment.simple("a"), APathSegment.parameterized("b", "keyB")).toString());
    }

    @Test
    public void testDecompose() {
        final APath p = APath.fromSegments(APathSegment.simple("a"), APathSegment.parameterized("b", "keyB"));

        assertEquals(APathSegment.parameterized("b", "keyB"), p.getLastSegment());
        assertEquals(APath.fromSegments(APathSegment.simple("a")), p.getParent());

        assertEquals(APathSegment.simple("a"), p.getParent().getLastSegment());

        assertEquals(APath.EMPTY, p.getParent().getParent());
    }
}
