package com.ajjpj.amapper.util.coll;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;


/**
 * @author arno
 */
public class AHashMapTest {
    @Test
    public void testSimple() {
        final AMap<Integer, Integer> m0 = AHashMap.empty();
        assertEquals(0, m0.size());
        assertEquals(true, m0.isEmpty());
        assertEquals(false, m0.nonEmpty());

        assertEquals(false, m0.containsKey(1));
        assertEquals(false, m0.get(1).isDefined());
        assertEquals(false, m0.containsKey(2));

        final AMap<Integer, Integer> m1 = m0.updated(1, 1);
        assertEquals(1, m1.size());
        assertEquals(false, m1.isEmpty());
        assertEquals(true, m1.nonEmpty());

        assertEquals(true, m1.containsKey(1));
        assertEquals(Integer.valueOf(1), m1.get(1).get());
        assertEquals(false, m1.containsKey(2));

        final AMap<Integer, Integer> m2 = m1.updated(2, 2);
        assertEquals(2, m2.size());
        assertEquals(false, m2.isEmpty());
        assertEquals(true, m2.nonEmpty());

        assertEquals(true, m2.containsKey(1));
        assertEquals(Integer.valueOf(1), m2.get(1).get());
        assertEquals(true, m2.containsKey(2));
        assertEquals(Integer.valueOf(2), m2.get(2).get());

        final AMap<Integer, Integer> m3 = m2.removed(1);
        assertEquals(1, m3.size());
        assertEquals(false, m3.isEmpty());
        assertEquals(true, m3.nonEmpty());

        assertEquals(false, m3.containsKey(1));
        assertEquals(true, m3.containsKey(2));
        assertEquals(Integer.valueOf(2), m3.get(2).get());

        final AMap<Integer, Integer> m4 = m3.removed(2);
        assertEquals(0, m4.size());
        assertEquals(true, m4.isEmpty());
        assertEquals(false, m4.nonEmpty());

        assertEquals(false, m4.containsKey(1));
        assertEquals(false, m4.get(1).isDefined());
        assertEquals(false, m4.containsKey(2));
    }

    final int size = 1000;
    final int numIter = 100*1000;

    private Map<Integer, Integer> createJu() {
        final Random rand = new Random(12345);
        final Map<Integer, Integer> result = new HashMap<Integer, Integer>();

        for(int i=0; i<numIter; i++) {
            final int key = rand.nextInt(size);
            final boolean add = rand.nextBoolean();

            if(add)
                result.put(key, key);
            else
                result.remove(key);
        }
        return result;
    }
    private Map<Integer, Integer> createConc() {
        final Random rand = new Random(12345);
        final Map<Integer, Integer> result = new ConcurrentHashMap<Integer, Integer>();

        for(int i=0; i<numIter; i++) {
            final int key = rand.nextInt(size);
            final boolean add = rand.nextBoolean();

            if(add)
                result.put(key, key);
            else
                result.remove(key);
        }
        return result;
    }
    private AMap<Integer, Integer> createA() {
        final Random rand = new Random(12345);
        AMap<Integer, Integer> result = AHashMap.empty();

        for(int i=0; i<numIter; i++) {
            final int key = rand.nextInt(size);
            final boolean add = rand.nextBoolean();

            if(add)
                result = result.updated(key, key);
            else
                result = result.removed(key);
        }
        return result;
    }

    private void doReadJu(Map<Integer, Integer> m) {
        for (int i=0; i<numIter; i++) {
            for(int j=0; j<size; j++) {
                m.get(j);
            }
        }
    }
    private void doReadA(AMap<Integer, Integer> m) {
        for (int i=0; i<numIter; i++) {
            for(int j=0; j<size; j++) {
                m.get(j);
            }
        }
    }

//TODO extract to a performance test suite
//    @Test
    public void testReadWritePerf() {
        doReadJu(createJu());
        doReadJu(createConc());
        doReadA(createA());

        final long t0 = System.currentTimeMillis();
        final Map<Integer, Integer> ju = createJu();
        final long t1 = System.currentTimeMillis();
        final Map<Integer, Integer> conc = createConc();
        final long t2 = System.currentTimeMillis();
        final AMap<Integer, Integer> a = createA();
        final long t3 = System.currentTimeMillis();
        doReadJu(ju);
        final long t4 = System.currentTimeMillis();
        doReadJu(conc);
        final long t5 = System.currentTimeMillis();
        doReadA(a);
        final long t6 = System.currentTimeMillis();

        System.out.println((t1 - t0));
        System.out.println((t2 - t1));
        System.out.println((t3 - t2));
        System.out.println((t4 - t3));
        System.out.println((t5 - t4));
        System.out.println((t6 - t5));
    }

    @Test
    public void testEquals() {
        assertEquals(AHashMap.empty(), AHashMap.empty());
        assertEquals(AHashMap.empty().hashCode(), AHashMap.empty().hashCode());

        assertEquals(AHashMap.empty().updated("a", "a1"),
                     AHashMap.empty().updated("a", "a1"));
        assertEquals(AHashMap.empty().updated("a", "a1").updated("b", "b1"),
                     AHashMap.empty().updated("a", "a1").updated("b", "b1"));
        assertEquals(AHashMap.empty().updated("a", "a1").updated("b", "b1").updated("a", "a2").removed("b"),
                     AHashMap.empty().updated("a", "a1").updated("b", "b1").updated("a", "a2").removed("b"));
        assertEquals(AHashMap.empty().updated("a", "a1").hashCode(),
                     AHashMap.empty().updated("a", "a1").hashCode());
        assertEquals(AHashMap.empty().updated("a", "a1").updated("b", "b1").hashCode(),
                     AHashMap.empty().updated("a", "a1").updated("b", "b1").hashCode());
        assertEquals(AHashMap.empty().updated("a", "a1").updated("b", "b1").updated("a", "a2").removed("b").hashCode(),
                     AHashMap.empty().updated("a", "a1").updated("b", "b1").updated("a", "a2").removed("b").hashCode());

        assertEquals(AHashMap.empty().updated("a", "a").updated("b", "b"),
                     AHashMap.empty().updated("b", "b").updated("a", "a"));
        assertEquals(AHashMap.empty().updated("a", "a").updated("b", "b").hashCode(),
                     AHashMap.empty().updated("b", "b").updated("a", "a").hashCode());

        assertNotEquals(AHashMap.empty().updated("a", "1"),
                        AHashMap.empty().updated("a", "2"));
        assertNotEquals(AHashMap.empty().updated("a", "1"),
                        AHashMap.empty().updated("b", "1"));
        assertNotEquals(AHashMap.empty().updated("a", "1").hashCode(),
                        AHashMap.empty().updated("a", "2").hashCode());
        assertNotEquals(AHashMap.empty().updated("a", "1").hashCode(),
                        AHashMap.empty().updated("b", "1").hashCode());
    }

    //TODO equality

    @Test
    public void testShotgun() {
        final Random rand = new Random(12345);

        final Map<Integer, Integer> ju = new HashMap<Integer, Integer>();
        AMap<Integer, Integer> a = AHashMap.empty();

        for(int i=0; i<1000*1000; i++) {
            final int key = rand.nextInt(100*1000);
            final boolean add = rand.nextBoolean();

            if(add) {
                ju.put(key, key);
                a = a.updated(key, key);
            }
            else {
                ju.remove(key);
                a = a.removed(key);
            }
//            System.out.println(i + ": " + key + " / " + add);
            assertEquals(ju.size(), a.size());
        }

        for(int key: ju.keySet()) {
            assertEquals(AOption.some(key), a.get(key));
        }
    }
}
