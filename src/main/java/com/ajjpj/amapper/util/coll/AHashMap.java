package com.ajjpj.amapper.util.coll;


/**
 * This is an immutable hash map based on 32-way hash tries.
 *
 * @author arno
 */
public class AHashMap<K, V> implements AMap<K,V> {
    private static final int LEVEL_INCREMENT = 5;

    final AEquality equality;

    private static final AHashMap<Object, Object> emptyEquals = new AHashMap<Object, Object>(AEquality.EQUALS);
    private static final AHashMap<Object, Object> emptyIdentity = new AHashMap<Object, Object>(AEquality.IDENTITY);

    public static <K,V> AHashMap<K,V> empty() {
        return empty(AEquality.EQUALS);
    }

    @SuppressWarnings("unchecked")
    public static <K,V> AHashMap<K,V> empty(AEquality equality) {
        if(equality == AEquality.EQUALS) return (AHashMap<K, V>) emptyEquals;
        if(equality == AEquality.IDENTITY) return (AHashMap<K, V>) emptyIdentity;
        return new AHashMap<K, V>(equality);
    }


    public AHashMap(AEquality equality) {
        this.equality = equality;
    }

    public int size() {
        return 0;
    }
    public boolean isEmpty() {
        return size() == 0;
    }
    public boolean nonEmpty() {
        return size() > 0;
    }

    public boolean containsKey(K key) {
        return get(key).isDefined();
    }

    public AOption<V> get(K key) {
        return doGet(key, computeHash(key, equality), 0);
    }

    /**
     * @param level number of least significant bits of the hash to discard for local hash lookup. This mechanism
     *              is used to create a 32-way hash trie - level increases by 5 at each level
     */
    AOption<V> doGet(K key, int hash, int level) {
        return AOption.none();
    }

    public AHashMap<K,V> updated(K key, V value) {
        return doUpdated(key, computeHash(key, equality), 0, value);
    }

    public AHashMap<K,V> removed(K key) {
        return doRemoved(key, computeHash(key, equality), 0);
    }

    AHashMap<K,V> doUpdated(K key, int hash, int level, V value) {
        return new HashMap1<K,V> (key, hash, value, equality);
    }

    AHashMap<K,V> doRemoved(K key, int hash, int level) {
        return this;
    }

    private static int computeHash(Object key, AEquality equality) {
        int h = equality.hashCode(key);
        h = h + ~(h << 9);
        h = h ^ (h >>> 14);
        h = h + (h << 4);
        return h ^ (h >>> 10);
    }

    @SuppressWarnings("unchecked")
    private static <K,V> AHashMap<K,V>[] createArray(int size) {
        return new AHashMap[size];
    }


    /**
     * very internal method. It assumes hash0 != hash1.
     */
    private static<K,V> HashTrieMap<K,V> mergeLeafMaps(int hash0, AHashMap<K,V> elem0, int hash1, AHashMap<K,V> elem1, int level, int size, AEquality equality) {
        final int index0 = (hash0 >>> level) & 0x1f;
        final int index1 = (hash1 >>> level) & 0x1f;
        if(index0 != index1) {
            final int bitmap = (1 << index0) | (1 << index1);
            final AHashMap<K,V>[] elems = createArray(2);
            if(index0 < index1) {
                elems[0] = elem0;
                elems[1] = elem1;
            }
            else {
                elems[0] = elem1;
                elems[1] = elem0;
            }
            return new HashTrieMap<K,V>(bitmap, elems, size, equality);
        }
        else {
            final AHashMap<K,V>[] elems = createArray(1);
            final int bitmap = (1 << index0);
            // try again, based on the
            elems[0] = mergeLeafMaps(hash0, elem0, hash1, elem1, level + LEVEL_INCREMENT, size, equality);
            return new HashTrieMap<K,V>(bitmap, elems, size, equality);
        }
    }


    static class HashMap1<K,V> extends AHashMap<K,V> {
        private final K key;
        private final int hash;
        private final V value;

        HashMap1(K key, int hash, V value, AEquality equality) {
            super(equality);

            this.key = key;
            this.hash = hash;
            this.value = value;
        }

        @Override AOption<V> doGet(K key, int hash, int level) {
            if(this.key.equals (key)) {
                return AOption.some(value);
            }
            return AOption.none();
        }

        @Override AHashMap<K,V> doUpdated(K key, int hash, int level, V value) {
            if (hash == this.hash && key.equals(this.key)) {
                if(this.value == value) {
                    return this;
                }
                else {
                    return new HashMap1<K,V>(key, hash, value, equality);
                }
            }
            else {
                if (hash != this.hash) {
                    // they have different hashes, but may collide at this level - find a level at which they don't
                    final AHashMap<K,V> that = new HashMap1<K,V>(key, hash, value, equality);
                    return mergeLeafMaps(this.hash, this, hash, that, level, 2, equality);
                }
                else {
                    // hash collision --> store all elements in the same bin
                    return new HashMapCollision1<K,V> (hash, AListMap.<K,V>empty(equality).updated(this.key,this.value).updated(key,value));
                }
            }
        }

        @Override AHashMap<K,V> doRemoved(K key, int hash, int level) {
            if (hash == this.hash && key.equals(this.key)) {
                return empty(equality);
            }
            else {
                return this;
            }
        }
    }

    static class HashMapCollision1<K,V> extends AHashMap<K,V> {
        private final int hash;
        private final AListMap<K,V> kvs;

        HashMapCollision1(int hash, AListMap<K, V> kvs) {
            super(kvs.equality);

            this.hash = hash;
            this.kvs = kvs;
        }

        @Override public int size() {
            return kvs.size();
        }

        @Override AOption<V> doGet(K key, int hash, int level) {
            if (hash == this.hash) {
                return kvs.get(key);
            }
            else {
                return AOption.none();
            }
        }

        @Override AHashMap<K,V> doUpdated(K key, int hash, int level, V value) {
            if (hash == this.hash) {
                return new HashMapCollision1<K,V>(hash, kvs.updated(key, value));
            }
            else {
                final HashMap1<K,V> that = new HashMap1<K,V>(key, hash, value, equality);
                return mergeLeafMaps(this.hash, this, hash, that, level, size() + 1, equality);
            }
        }

        @Override AHashMap<K,V> doRemoved(K key, int hash, int level) {
            if (hash == this.hash) {
                final AListMap<K,V> kvs1 = kvs.removed(key);
                if (kvs1.isEmpty()) {
                    return AHashMap.empty(equality);
                }
                else if(kvs1.tail().isEmpty()) {
                    return new HashMap1<K,V>(kvs1.key(), computeHash(kvs1.key(), equality), kvs1.value(), equality);
                }
                else {
                    return new HashMapCollision1<K,V>(hash, kvs1);
                }
            }
            else {
                return this;
            }
        }
    }


    static class HashTrieMap<K,V> extends AHashMap<K,V> {
        final int bitmap;
        final AHashMap<K,V>[] elems;
        final int size;

        HashTrieMap(int bitmap, AHashMap<K, V>[] elems, int size, AEquality equality) {
            super(equality);

            this.bitmap = bitmap;
            this.elems = elems;
            this.size = size;
        }

        @Override public int size() {
            return size;
        }

        @Override AOption<V> doGet(K key, int hash, int level) {
            final int index = (hash >>> level) & 0x1f;
            final int mask = 1 << index;

            if (bitmap == - 1) {
                return elems[index & 0x1f].doGet(key, hash, level + LEVEL_INCREMENT);
            }
            else if ((bitmap & mask) != 0) {
                final int offset = Integer.bitCount(bitmap & (mask-1));
                return elems[offset].doGet(key, hash, level + LEVEL_INCREMENT);
            }
            else {
                return AOption.none();
            }
        }

        @Override AHashMap<K,V>  doUpdated(K key, int hash, int level, V value) {
            final int index = (hash >>> level) & 0x1f;
            final int mask = (1 << index);
            final int offset = Integer.bitCount(bitmap & (mask-1));
            if ((bitmap & mask) != 0) {
                final AHashMap<K,V> sub = elems[offset];

                final AHashMap<K,V> subNew = sub.doUpdated(key, hash, level + LEVEL_INCREMENT, value);
                if(subNew == sub) {
                    return this;
                }
                else {
                    final AHashMap<K,V>[] elemsNew = createArray(elems.length);
                    System.arraycopy(elems, 0, elemsNew, 0, elems.length);
                    elemsNew[offset] = subNew;
                    return new HashTrieMap<K,V> (bitmap, elemsNew, size + (subNew.size() - sub.size()), equality);
                }
            }
            else {
                final AHashMap<K,V>[] elemsNew = createArray(elems.length + 1);
                System.arraycopy(elems, 0, elemsNew, 0, offset);
                elemsNew[offset] = new HashMap1<K,V>(key, hash, value, equality);
                System.arraycopy(elems, offset, elemsNew, offset + 1, elems.length - offset);
                return new HashTrieMap<K,V>(bitmap | mask, elemsNew, size + 1, equality);
            }
        }

        @Override AHashMap<K,V> doRemoved(K key, int hash, int level) {
            final int index = (hash >>> level) & 0x1f;
            final int mask = (1 << index);
            final int  offset = Integer.bitCount(bitmap & (mask-1));

            if ((bitmap & mask) != 0) {
                final AHashMap<K,V> sub = elems[offset];
                final AHashMap<K,V> subNew = sub.doRemoved(key, hash, level + LEVEL_INCREMENT);

                if (subNew == sub) {
                    return this;
                }
                else if (subNew.isEmpty()) {
                    final int  bitmapNew = bitmap ^ mask;
                    if (bitmapNew != 0) {
                        final AHashMap<K,V>[] elemsNew = createArray(elems.length - 1);
                        System.arraycopy(elems, 0, elemsNew, 0, offset);
                        System.arraycopy(elems, offset + 1, elemsNew, offset, elems.length - offset - 1);
                        final int sizeNew = size - sub.size();
                        if (elemsNew.length == 1 && ! (elemsNew[0] instanceof HashTrieMap)) {
                            return elemsNew[0];
                        }
                        else {
                            return new HashTrieMap<K,V>(bitmapNew, elemsNew, sizeNew, equality);
                        }
                    }
                    else {
                        return AHashMap.empty(equality);
                    }
                }
                else if(elems.length == 1 && ! (subNew instanceof HashTrieMap)) {
                    return subNew;
                }
                else {
                    final AHashMap<K,V>[] elemsNew = createArray(elems.length);
                    System.arraycopy(elems, 0, elemsNew, 0, elems.length);
                    elemsNew[offset] = subNew;
                    final int sizeNew = size + (subNew.size() - sub.size());
                    return new HashTrieMap<K,V>(bitmap, elemsNew, sizeNew, equality);
                }
            } else {
                return this;
            }
        }
    }
}
