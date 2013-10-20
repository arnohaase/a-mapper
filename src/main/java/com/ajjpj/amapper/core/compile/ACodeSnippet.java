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

    private final Collection<AInjectedField> injectedFields;
    private final String code;

    public ACodeSnippet(String code) {
        this(code, Collections.<AInjectedField>emptyList());
    }

    public ACodeSnippet(String code, Collection<AInjectedField> injectedFields) {
        this.code = code;
        this.injectedFields = Collections.unmodifiableCollection(injectedFields);
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
