package com.ajjpj.amapper.core.diff;

import com.ajjpj.abase.collection.immutable.AList;

import java.util.ArrayList;
import java.util.List;


/**
 * @author arno
 */
public class ADiffBuilder {
    private final List<ADiffElement> elements = new ArrayList<>();

    public void add(ADiffElement diffElement) {
        elements.add (diffElement);
    }

    public ADiff build() {
        return new ADiff(AList.create(elements));
    }
}
