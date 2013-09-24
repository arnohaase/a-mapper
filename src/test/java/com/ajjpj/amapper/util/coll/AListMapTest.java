package com.ajjpj.amapper.util.coll;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;


/**
 * @author arno
 */
public class AListMapTest {
    @Test
    public void testSimple() {
        final AMap<Integer, Integer> m0 = AListMap.empty();
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

    @Test
    public void testShotgun() {
        final Random rand = new Random(12345);

        final Map<Integer, Integer> ju = new HashMap<Integer, Integer>();
        AMap<Integer, Integer> a = AListMap.empty();

        for(int i=0; i<100; i++) {
            final int key = rand.nextInt(10);
            final boolean add = rand.nextBoolean();

//            System.out.println(i + ": " + key + " / " + add);

            if(add) {
                ju.put(key, key);
                a = a.updated(key, key);
            }
            else {
                ju.remove(key);
                a = a.removed(key);
            }
            assertEquals(ju.size(), a.size());
        }

        for(int key: ju.keySet()) {
            assertEquals(AOption.some(key), a.get(key));
        }
    }
}
