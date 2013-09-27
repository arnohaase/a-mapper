package com.ajjpj.amapper.javabean2.propbased;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class represents 'synthetic', explicitly provided partial mappings. It is for those rare cases for which there is no
 *  natural representation in terms of 'source and target properties'.
 *
 * @author arno
 */
public abstract class AExplicitPartialMapping<S,T,H> implements APartialBeanMapping <S,T,H> {
    private static final AtomicInteger count = new AtomicInteger();

    private final String name = "synthetic-" + count.incrementAndGet();

    @Override public String getSourceName() {
        return name;
    }

    @Override public String getTargetName() {
        return name;
    }
}
