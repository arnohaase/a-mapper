package com.ajjpj.amapper.core.diff;

import com.ajjpj.amapper.core.path.APath;

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

    @Override
    public String toString() {
        return "ADiffElement{" +
                "kind=" + kind +
                ", path=" + path +
                ", isDerived=" + isDerived +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ADiffElement that = (ADiffElement) o;

        if (isDerived != that.isDerived) return false;
        if (kind != that.kind) return false;
        if (newValue != null ? !newValue.equals(that.newValue) : that.newValue != null) return false;
        if (oldValue != null ? !oldValue.equals(that.oldValue) : that.oldValue != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = kind != null ? kind.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (isDerived ? 1 : 0);
        result = 31 * result + (oldValue != null ? oldValue.hashCode() : 0);
        result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
        return result;
    }
}
