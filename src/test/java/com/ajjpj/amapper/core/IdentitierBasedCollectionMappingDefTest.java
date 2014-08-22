package com.ajjpj.amapper.core;

import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.abase.function.AFunction2NoThrow;
import com.ajjpj.amapper.collection.LevenshteinDistance;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;


/**
 * @author bitmagier
 */
public class IdentitierBasedCollectionMappingDefTest {


    @Test public void testLevenshteinDistance() throws Exception {
        final Collection<Integer> source = Arrays.asList (4,5,6,7,8,9);
        final List<String> target = new ArrayList<> (Arrays.asList ("3","6","7","8","4"));


        final AFunction2NoThrow<Integer, String, Boolean> eqFunction = new AFunction2NoThrow<Integer, String, Boolean> () {
            @Override public Boolean apply (Integer param1, String param2) {
                return Integer.valueOf (param2).equals (param1);
            }
        };

        final AFunction2NoThrow<Integer, String, AOption <String>> mapFunction = new AFunction2NoThrow<Integer, String, AOption<String>> () {
            @Override public AOption<String> apply (Integer param1, String param2) {
                return AOption.some(param1.toString());
            }
        };

        final LevenshteinDistance<Integer, String> lev = new LevenshteinDistance<> (source, target, eqFunction, mapFunction);
        int steps = lev.editTarget();

        assertEquals (3, steps);

        final List<String> expectedTarget = new ArrayList<>();
        for (Integer s: source) {
            expectedTarget.add (mapFunction.apply (s, null).get());
        }
        assertArrayEquals (expectedTarget.toArray(), target.toArray ());
    }
}
