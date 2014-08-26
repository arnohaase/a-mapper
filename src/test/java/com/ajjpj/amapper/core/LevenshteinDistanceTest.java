package com.ajjpj.amapper.core;

import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.abase.function.AFunction2NoThrow;
import com.ajjpj.abase.function.APredicate2NoThrow;
import com.ajjpj.amapper.collection.LevenshteinDistance;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


/**
 * @author Roman
 */
public class LevenshteinDistanceTest {

    @Test public void testLevenshteinMap1() throws Exception {
        final Collection<Integer> source = Arrays.asList (4,5,6,7,8,9);
        final List<String> target = new ArrayList<> (Arrays.asList ("3","6","7","8","4"));


        final APredicate2NoThrow<Integer, String> eq = new APredicate2NoThrow<Integer, String> () {
            @Override public boolean apply (Integer param1, String param2) {
                return Integer.valueOf (param2).equals (param1);
            }
        };

        final AFunction2NoThrow<Integer, String, AOption <String>> mapFunction = new AFunction2NoThrow<Integer, String, AOption<String>> () {
            @Override public AOption<String> apply (Integer param1, String param2) {
                return AOption.some(param1.toString());
            }
        };

        final LevenshteinDistance<Integer, String> lev = new LevenshteinDistance<> (source, target, eq);
        final int steps = lev.editTarget (mapFunction);

        assertEquals (3, steps);

        final List<String> expectedTarget = new ArrayList<>();
        for (Integer s: source) {
            expectedTarget.add (mapFunction.apply (s, null).get());
        }
        assertArrayEquals (expectedTarget.toArray(), target.toArray ());
    }

    @Test public void testLevenshteinMap2() throws Exception {
        final List<Character> source = Arrays.asList ('p','h','y','s','a','l','i','s');
        final List<Character> target = new ArrayList (Arrays.asList ('p','h','o','s','p','h','o','r'));

        final APredicate2NoThrow<Character, Character> eq = new APredicate2NoThrow<Character, Character> () {
            @Override public boolean apply (Character param1, Character param2) {
                return param1.equals (param2);
            }
        };

        final AFunction2NoThrow<Character, Character, AOption<Character>> mapFunction = new AFunction2NoThrow<Character, Character, AOption<Character>> () {
            @Override public AOption<Character> apply (Character character1, Character character2) {
                return AOption.some (character1);
            }
        };

        final LevenshteinDistance<Character, Character> lev = new LevenshteinDistance<> (source, target, eq);
        final int steps = lev.editTarget (mapFunction);

        assertEquals (5, steps);

        assertArrayEquals (source.toArray (), target.toArray ());
    }

    @Test public void testLevenshteinMap3() throws Exception {

        class MyClassA {
            final int id;
            final String value;

            MyClassA (int id, String value) {
                this.id = id;
                this.value = value;
            }
        }

        class MyClassB {
            final int id;
            final String value;

            MyClassB (int id, String value) {
                this.id = id;
                this.value = value;
            }

            @Override
            public boolean equals (Object o) {
                if ( this == o ) return true;
                if ( o == null || getClass () != o.getClass () ) return false;

                MyClassB myClassB = (MyClassB) o;

                if ( id != myClassB.id ) return false;
                if ( value != null ? !value.equals (myClassB.value) : myClassB.value != null ) return false;

                return true;
            }

            @Override
            public int hashCode () {
                int result = id;
                result = 31 * result + (value != null ? value.hashCode () : 0);
                return result;
            }
        }

        final List<MyClassA> source = new ArrayList<>();
        source.add (new MyClassA (1, "A"));
        source.add (new MyClassA (2, "B"));
        source.add (new MyClassA (3, "X"));

        final List<MyClassB> target = new ArrayList<>();
        target.add (new MyClassB (1, "A"));
        target.add (new MyClassB (5, "_"));
        target.add (new MyClassB (3, "Z"));

        final APredicate2NoThrow<MyClassA, MyClassB> eq = new APredicate2NoThrow<MyClassA, MyClassB> () {
            @Override public boolean apply (MyClassA a, MyClassB b) {
                return a.id == b.id;
            }
        };

        final AFunction2NoThrow<MyClassA, MyClassB, AOption<MyClassB>> mapFunction = new AFunction2NoThrow<MyClassA, MyClassB, AOption<MyClassB>> () {
            @Override public AOption<MyClassB> apply (MyClassA myClassA, MyClassB myClassB) {
                return AOption.some(new MyClassB (myClassA.id, myClassA.value));
            }
        };

        final LevenshteinDistance<MyClassA, MyClassB> lev = new LevenshteinDistance<> (source, target, eq);
        final int steps = lev.editTarget (mapFunction);

        assertEquals (1, steps);
        assertEquals (Arrays.asList (new MyClassB (1, "A"), new MyClassB (2, "B"), new MyClassB (3, "X")), target);
    }

}
