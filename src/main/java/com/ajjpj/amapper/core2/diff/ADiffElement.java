package com.ajjpj.amapper.core2.diff;

import com.ajjpj.amapper.core2.path.APath;

/**
 * @author arno
 */
public class ADiffElement {
    public enum Kind {
        Attribute, RefChange, Add, Remove
    }

    public final Kind kind;

    public final APath path;

    /**
     * marks changes that were caused by structural changes further up in the graph
     */
    public final boolean isDerived;

    /**
     * from the <em>target</em> perspective
     */
    public final Object oldValue;

    /**
     * from the <em>target</em> perspective
     */
    public final Object newValue;

    public static ADiffElement attribute(APath path, boolean isDerived, Object oldValue, Object newValue) {
        return new ADiffElement(Kind.Attribute, path, isDerived, oldValue, newValue);
    }

    public static ADiffElement refChanged(APath path, boolean isDerived, Object oldValue, Object newValue) {
        return new ADiffElement(Kind.RefChange, path, isDerived, oldValue, newValue);
    }

    public static ADiffElement added(APath path, boolean isDerived, Object newValue) {
        return new ADiffElement(Kind.Add, path, isDerived, null, newValue);
    }

    public static ADiffElement removed(APath path, boolean isDerived, Object oldValue) {
        return new ADiffElement(Kind.Remove, path, isDerived, oldValue, null);
    }


    private ADiffElement(Kind kind, APath path, boolean isDerived, Object oldValue, Object newValue) {
        this.kind = kind;
        this.path = path;
        this.isDerived = isDerived;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
