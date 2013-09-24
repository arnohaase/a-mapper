package com.ajjpj.amapper.util.coll;

import java.util.NoSuchElementException;

/**
 * @author arno
 */
public class AListMap <K,V> implements AMap<K,V> {
    public static <K,V> AListMap<K,V> empty() {
        return new AListMap<K, V>();
    }
    public static <K,V> AListMap<K,V> empty(AEquality equality) {
        return new AListMap<K, V> (equality);
    }

    public static <K,V> AListMap<K,V> create(Iterable<APair<K,V>> elements) {
        return create(AEquality.EQUALS, elements);
    }
    public static <K,V> AListMap<K,V> create(AEquality equality, Iterable<APair<K,V>> elements) {
        AListMap<K,V> result = empty(equality);

        for(APair<K,V> el: elements) {
            result = result.updated(el._1, el._2);
        }
        return result;
    }

    final AEquality equality;

    public AListMap() {
        this(AEquality.EQUALS);
    }
    public AListMap(AEquality equality) {
        this.equality = equality;
    }

    public int size() {
        return 0;
    }
    public boolean isEmpty() {
        return true;
    }
    public boolean nonEmpty() {
        return false;
    }

    public AOption<V> get(K key) {
        return AOption.none();
    }

    public K key() {
        throw new NoSuchElementException("empty map");
    }

    public V value() {
        throw new NoSuchElementException("empty map");
    }

    public boolean containsKey(K key) {
        return get(key).isDefined();
    }

    public AListMap<K,V> updated(K key, V value) {
        return new Node<K,V>(key, value, this);
    }

    public AListMap<K,V> removed(K key) {
        return this;
    }

    public AListMap<K,V> tail() {
        throw new NoSuchElementException("empty map");
    }


    static class Node<K,V> extends AListMap<K,V> {
        private final K key;
        private final V value;
        private final AListMap<K,V> tail;

        Node(K key, V value, AListMap<K, V> tail) {
            super(tail.equality);

            this.key = key;
            this.value = value;
            this.tail = tail;
        }

        @Override public boolean isEmpty() {
            return false;
        }
        @Override public boolean nonEmpty() {
            return true;
        }

        @Override public K key() {
            return key;
        }
        @Override public V value() {
            return value;
        }
        @Override public AListMap<K,V> tail() {
            return tail;
        }

        @Override public int size() {
            int result = 0;

            AListMap<K,V> m = this;
            while(m.nonEmpty()) {
                m = m.tail();
                result += 1;
            }
            return result;
        }

        @Override public AOption<V> get(K key) {
            AListMap<K,V> m = this;

            while(m.nonEmpty()) {
                if(equality.equals(m.key(), key)) {
                    return AOption.some(m.value());
                }
                m = m.tail();
            }
            return AOption.none();
        }

        @Override public AListMap<K,V> updated(K key, V value) {
            final AListMap<K,V> m = removed(key);
            return new Node<K,V>(key, value, m);
        }

        @Override public AListMap<K,V> removed(K key) {
            AList<APair<K,V>> raw = AList.nil();
            AListMap<K,V> remaining = this;

            while(remaining.nonEmpty()) {
                if(! equality.equals(remaining.key(), key)) {
                    raw = raw.cons(new APair<K,V>(remaining.key(), remaining.value())); //TODO terminate - the key should have been unique
                }
                remaining = remaining.tail();
            }

            return AListMap.create(raw.reverse().asJavaUtilList());
        }
    }
}
