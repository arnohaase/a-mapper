package com.ajjpj.amapper.util.coll;


/**
 * @author arno
 */
public interface AMap<K,V> {
    int size();
    boolean isEmpty();
    boolean nonEmpty();

    boolean containsKey(K key);
    AOption<V> get(K key);

    AMap<K,V> updated(K key, V value);
    AMap<K,V> removed(K key);

    //TODO add methods
    // ASet<K> keys();
    // elements();

    //TODO ACollection
}
