package com.ajjpj.amapper.util.coll;


import java.util.Iterator;

/**
 * @author arno
 */
public interface AMap<K,V> extends Iterable<APair<K,V>> {
    int size();
    boolean isEmpty();
    boolean nonEmpty();

    boolean containsKey(K key);
    boolean containsValue(V value);
    AOption<V> get(K key);

    AMap<K,V> updated(K key, V value);
    AMap<K,V> removed(K key);

    @Override Iterator<APair<K,V>> iterator();

    java.util.Map<K,V> asJavaUtilMap();

    //TODO withDefault, withDefaultValue
    //TODO toString implementations
    //TODO equals implementations for collections?
}
