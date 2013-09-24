package com.ajjpj.amapper.util.coll;


/**
 * @author arno
 */
public class AHashSet<T> {
    private final AHashMap<T, Boolean> inner;

    public static <T> AHashSet<T> empty(AEquality equality) {
        return new AHashSet<T>(AHashMap.<T, Boolean>empty(equality));
    }

    private AHashSet(AHashMap<T, Boolean> inner) {
        this.inner = inner;
    }

    public boolean contains(T el) {
        return inner.containsKey(el);
    }

    public AHashSet<T> added(T el) {
        return new AHashSet<T> (inner.updated(el, true));
    }

    public AHashSet<T> removed(T el) {
        return new AHashSet<T> (inner.removed(el));
    }
}
