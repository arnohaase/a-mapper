package com.ajjpj.amapper.core.diff;

import com.ajjpj.abase.collection.immutable.*;
import com.ajjpj.abase.function.AFunction1;
import com.ajjpj.abase.function.AFunction1NoThrow;
import com.ajjpj.abase.function.APredicate;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.path.APathSegment;

/**
 * @author arno
 */
public class ADiff {
    private final AList<ADiffElement> elements;

    public final AHashSet<APath> paths;
    public final AMap<APath, ADiffElement> byPath;
    public final AHashSet<String> pathStrings;
    public final AMap<String, AList<ADiffElement>> byPathString;

    public ADiff(final AList<ADiffElement> elements) {
        this.elements = elements;
        this.paths = elements.map(new AFunction1<ADiffElement, APath, RuntimeException>() { //TODO nothrow
            @Override
            public APath apply(ADiffElement param) {
                return param.path;
            }
        }).toSet();
        this.byPath = AHashMap.fromKeysAndFunction(paths, new AFunction1<APath, ADiffElement, RuntimeException>() { //TODO nothrow
            @Override
            public ADiffElement apply(final APath path) {
                return elements.find(new APredicate<ADiffElement, RuntimeException>() {
                    @Override
                    public boolean apply(ADiffElement o) {
                        return o.path.equals(path);
                    }
                }).get();
            }
        });
        this.pathStrings = paths.map(new AFunction1<APath, String, RuntimeException>() { //TODO nothrow
            @Override
            public String apply(APath param) {
                return asPathString(param);
            }
        });
        this.byPathString = AHashMap.fromKeysAndFunction(pathStrings, new AFunction1<String, AList<ADiffElement>, RuntimeException>() { //TODO nothrow
            @Override public AList<ADiffElement> apply(final String path) {
                return elements.filter(new APredicate<ADiffElement, RuntimeException>() {
                    @Override public boolean apply(ADiffElement o) {
                        return asPathString(o.path).equals(path);
                    }
                });
            }
        });
    }

    private static String asPathString(APath p) {
        final StringBuilder result = new StringBuilder();
        boolean first = true;

        for(APathSegment seg: p.getSegments()) {
            if(first) {
                first = false;
            }
            else {
                result.append(".");
            }
            result.append(seg.getName());
        }
        return result.toString();
    }

    public AList<ADiffElement> getElements() {
        return elements;
    }

    public AHashSet<APath> getPaths() {
        return paths;
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public AOption<ADiffElement> getSingle(APath path) {
        return byPath.get(path);
    }

    public AOption<ADiffElement> getSingle(String path) {
        final AOption<AList<ADiffElement>> all = byPathString.get(path);

        if(all.isDefined() && all.get().nonEmpty()) {
            return AOption.some(all.get().head());
        }
        else {
            return AOption.none();
        }
    }

    @Override
    public String toString() {
        return "ADiff[" + elements + "]";
    }
}
