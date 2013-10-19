package com.ajjpj.amapper.core.compile;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author arno
 */
public class ACodeSnippet {
    private static AtomicLong identCounter = new AtomicLong();
    public static String uniqueIdentifier() {
        return "_o_" + identCounter.incrementAndGet();
    }

    private final Collection<String> supports;
    private final Collection<AInjectedField> injectedFields;
    private final String code;

    public ACodeSnippet(String code) {
        this(code, Collections.<String>emptyList(), Collections.<AInjectedField>emptyList());
    }

    public ACodeSnippet(String code, Collection<String> supports, Collection<AInjectedField> injectedFields) {
        this.code = code;
        this.supports = Collections.unmodifiableCollection(supports);
        this.injectedFields = Collections.unmodifiableCollection(injectedFields);
    }

    /**
     * @return declarative code snippets required to support the actual code, e.g. variable declarations for caching.
     *  These snippets are guaranteed to be in scope for the actual code. They will be placed in a class but outside
     *  of all methods.
     */
    public Collection<String> getSupports() {
        return supports;
    }

    public Collection<AInjectedField> getInjectedFields() {
        return injectedFields;
    }

    /**
     * @return the actual Java code
     */
    public String getCode() {
        return code;
    }
}
