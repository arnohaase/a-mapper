package com.ajjpj.amapper.core2.path;

import com.ajjpj.amapper.util.coll.AList;

import java.util.Arrays;

/**
 * @author arno
 */
public class APath {
    public static final APath EMPTY = new APath(AList.<APathSegment> nil());

    // root is last, leaf is head
    public final AList<APathSegment> segments;

    public APath(AList<APathSegment> segments) {
        this.segments = segments;
    }

    public APath(APathSegment... segments) {
        this(AList.create(Arrays.asList(segments)));
    }

    public APath withChild (APathSegment child) {
        return new APath (segments.cons(child));
    }

    public APath getParent() {
        return new APath(segments.tail());
    }

    public APathSegment getLastSegment() {
        return segments.head();
    }

    @Override
    public String toString() {
        return "APath{" + segments.mkString(".") + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        APath aPath = (APath) o;

        if (segments != null ? !segments.equals(aPath.segments) : aPath.segments != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return segments != null ? segments.hashCode() : 0;
    }
}
