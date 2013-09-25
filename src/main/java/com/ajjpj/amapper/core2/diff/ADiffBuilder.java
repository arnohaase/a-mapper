package com.ajjpj.amapper.core2.diff;

import com.ajjpj.amapper.util.coll.AList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arno
 */
public class ADiffBuilder {
    private final List<ADiffElement> elements = new ArrayList<ADiffElement>();

    public void add(ADiffElement diffElement) {
        elements.add (diffElement);
    }

    public ADiff build() {
        return new ADiff(AList.create(elements));
    }
}
