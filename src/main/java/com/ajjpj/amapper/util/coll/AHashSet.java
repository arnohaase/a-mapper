package com.ajjpj.amapper.util.coll;


import com.ajjpj.amapper.util.func.AFunction1;
import com.ajjpj.amapper.util.func.APredicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author arno
 */
public class AHashSet<T> implements Iterable<T> {
    private final AHashMap<T, Boolean> inner;

    public static <T> AHashSet<T> empty() {
        return empty(AEquality.EQUALS);
    }
    public static <T> AHashSet<T> empty(AEquality equality) {
        return new AHashSet<T>(AHashMap.<T, Boolean>empty(equality));
    }

    public static <T> AHashSet<T> create(Iterable<T> elements) {
        return create(AEquality.EQUALS, elements);
    }
    public static <T> AHashSet<T> create(AEquality equality, Iterable<T> elements) {
        AHashSet<T> result = empty(equality);
        for(T el: elements) {
            result = result.added(el);
        }
        return result;
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

    public java.util.Set<T> asJavaUtilSet() {
        return inner.asJavaUtilMap().keySet();
    }

    @Override public Iterator<T> iterator() {
        return asJavaUtilSet().iterator();
    }

    public AList<T> toList() {
        return AList.create(this);
    }

    public AOption<T> find(APredicate<T> pred) {
        for(T el: this) {
            if(pred.apply(el)) {
                return AOption.some(el);
            }
        }
        return AOption.none();
    }

    public <X> AHashSet<X> map(AFunction1<X, T> f) {
        final List<X> result = new ArrayList<X>(); // list instead of set to support arbitrary equality implementations
        for(T el: this) {
            result.add(f.apply(el));
        }
        return create(inner.equality, result);
    }

    public AHashSet<T> filter(APredicate<T> pred) {
        final List<T> result = new ArrayList<T>();
        for(T el: this) {
            if(pred.apply(el)) {
                result.add(el);
            }
        }
        return create(inner.equality, result);
    }
}
