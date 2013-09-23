package com.ajjpj.amapper.javabean.japi;


import com.ajjpj.amapper.core.*;
import com.ajjpj.amapper.javabean.BuiltinCollectionMappingDefs;
import com.ajjpj.amapper.javabean.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;

import java.util.concurrent.Callable;

/**
 * This is a builder class for Java Beans-based mappers. It provides a Java friendly API and is implemented
 *  in Java so as to facilitate reading by Java-only programmers.
 *
 * @author arno
 */
public class JavaBeanMapperBuilder<H extends JavaBeanMappingHelper> {
    // the actual builder for which this class acts as a facade
    private final com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder<H> inner;

    public static JavaBeanMapperBuilder<JavaBeanMappingHelper> create() {
        final Callable<JavaBeanMappingHelper> hf = JApi.simpleBeanMappingHelper();
        return new JavaBeanMapperBuilder<JavaBeanMappingHelper>(hf)
                .withObjectMapping(BuiltinCollectionMappingDefs.MergingListMappingDef())
                .withObjectMapping(BuiltinCollectionMappingDefs.MergingSetMappingDef());
    }

    public static <H extends JavaBeanMappingHelper> JavaBeanMapperBuilder<H> create (Callable<H> helperFactory) {
        return new JavaBeanMapperBuilder<H>(helperFactory);
    }

    public static <H extends JavaBeanMappingHelper> JavaBeanMapperBuilder<H> create (final H helper) {
        return new JavaBeanMapperBuilder<H>(new Callable<H>() {
            @Override public H call() {return helper;}
        });
    }

    public JavaBeanMapperBuilder(final Callable<H> helperFactory) {
        inner = com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder.create (JApi.asFunction(helperFactory));
    }

    public JavaBeanMapperBuilder<H> withValueMapping(AValueMappingDef<?, ?, ? super H> m) {
        inner.addValueMapping(m);
        return this;
    }

    public JavaBeanMapperBuilder<H> withObjectMapping(AObjectMappingDef<?, ?, ? super H> m) {
        inner.addObjectMapping(m);
        return this;
    }

    public JavaBeanMapperBuilder<H> withBeanMapping(JavaBeanMapping<?, ?> m) {
        inner.addBeanMapping(m);
        return this;
    }

    public JavaBeanMapperBuilder<H> withLogger(AMapperLogger logger) {
        inner.withLogger(logger);
        return this;
    }

    public JavaBeanMapperBuilder<H> withIdentifierExtractor(IdentifierExtractor identifierExtractor) {
        inner.withIdentifierExtractor(identifierExtractor);
        return this;
    }

    public JavaBeanMapperBuilder<H> withContextExtractor(AContextExtractor contextExtractor) {
        inner.withContextExtractor(contextExtractor);
        return this;
    }

    public JavaBeanMapper build() {
        return new JavaBeanMapperImpl(inner.build());
    }
}
