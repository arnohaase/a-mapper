package com.ajjpj.amapper.core.path;

import com.ajjpj.abase.collection.immutable.AList;

import java.io.Serializable;
import java.util.Arrays;


/**
 * @author arno
 */
public class APath implements Serializable {
    public static final APath EMPTY = new APath(AList.<APathSegment> nil());

    // root is last, leaf is head
    public final AList<APathSegment> reverseSegments;
    private AList<APathSegment> segments = null;

    public APath(AList<APathSegment> reverseSegments) {
        this.reverseSegments = reverseSegments;
    }

    public static APath fromSegments (APathSegment... segments) {
        return new APath(AList.create(Arrays.asList(segments)).reverse());
    }

    public APath withChild (APathSegment child) {
        return new APath (reverseSegments.cons(child));
    }

    public APath withElementChild (Object childIdentifier) {
        return withChild (APathSegment.parameterized ("elements", childIdentifier));
    }

    public APath getParent() {
        return new APath(reverseSegments.tail());
    }

    public APathSegment getLastSegment() {
        return reverseSegments.head();
    }

    public AList<APathSegment> getSegments() {
        // this is actually thread safe because 'segments' is immutable. It is possible for several threads
        //  to do this initialization independently, but they are guaranteed to get equivalent results.
        if(segments == null) {
            segments = reverseSegments.reverse();
        }
        return segments;
    }

    @Override
    public String toString() {
        return "APath{" + getSegments().mkString(".") + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        APath aPath = (APath) o;

        if (reverseSegments != null ? !reverseSegments.equals(aPath.reverseSegments) : aPath.reverseSegments != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return reverseSegments != null ? reverseSegments.hashCode() : 0;
    }
}
