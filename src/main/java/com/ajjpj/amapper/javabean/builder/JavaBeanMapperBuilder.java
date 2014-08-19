package com.ajjpj.amapper.javabean.builder;

import com.ajjpj.abase.function.AFunction0;
import com.ajjpj.amapper.core.*;
import com.ajjpj.amapper.core.exclog.AMapperExceptionHandler;
import com.ajjpj.amapper.core.exclog.AMapperLogger;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedType;
import com.ajjpj.amapper.javabean.AnnotationBasedContextExtractor;
import com.ajjpj.amapper.javabean.JavaBeanMapper;
import com.ajjpj.amapper.javabean.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean.SimpleJavaBeanMappingHelper;
import com.ajjpj.amapper.javabean.impl.JavaBeanMapperImpl;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinValueMappingDefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This builder is the typical starting point for creating an AMapper instance for Java Beans. It builds an instance
 *  of JavaBeanMapper, which is in turn a Java Bean friendly API wrapper around an AMapper instance.
 *
 * @author arno
 */
public class JavaBeanMapperBuilder <H extends JavaBeanMappingHelper> {
    private final List<AObjectMappingDef<?,?,? super H>> objectMappings = new ArrayList<AObjectMappingDef<?, ?, ? super H>>();
    private final List<AValueMappingDef <?,?,? super H>> valueMappings  = new ArrayList<AValueMappingDef <?, ?, ? super H>>(Arrays.asList(
            BuiltinValueMappingDefs.StringMappingDef, BuiltinValueMappingDefs.BooleanMappingDef, BuiltinValueMappingDefs.CharacterMappingDef,
            BuiltinValueMappingDefs.BigDecimalMappingDef, BuiltinValueMappingDefs.BigIntegerMappingDef,
            BuiltinValueMappingDefs.DateMappingDef, BuiltinValueMappingDefs.LocaleMappingDef, BuiltinValueMappingDefs.TimeZoneMappingDef, BuiltinValueMappingDefs.CurrencyMappingDef,
            BuiltinValueMappingDefs.UuidMappingDef,
            BuiltinValueMappingDefs.ClassMappingDef, BuiltinValueMappingDefs.EnumMappingDef,
            BuiltinValueMappingDefs.ByteMappingDef, BuiltinValueMappingDefs.ShortMappingDef, BuiltinValueMappingDefs.IntegerMappingDef, BuiltinValueMappingDefs.LongMappingDef,
            BuiltinValueMappingDefs.FloatMappingDef, BuiltinValueMappingDefs.DoubleMappingDef
    ));

    private AMapperLogger logger = AMapperLogger.defaultLogger();
    private AFunction0<H, RuntimeException> helperFactory = new AFunction0<H, RuntimeException>() {
        @SuppressWarnings("unchecked")
        @Override public H apply() {
            return (H) SimpleJavaBeanMappingHelper.INSTANCE;
        }
    };
    private AIdentifierExtractor identifierExtractor = new AIdentifierExtractor() {
        @Override
        public Object uniqueIdentifier(Object o, AQualifiedType type, AQualifiedType targetType) {
            return o == null ? null : o.toString();
        }
    };
    private AContextExtractor contextExtractor = new AnnotationBasedContextExtractor();

    private final List<APreProcessor> preProcessors = new ArrayList<APreProcessor>();
    private final List<APostProcessor> postProcessors = new ArrayList<APostProcessor>();

    //---------------------------------

    public static JavaBeanMapperBuilder<JavaBeanMappingHelper> create() {
        return new JavaBeanMapperBuilder<JavaBeanMappingHelper>();
    }

    public JavaBeanMapperBuilder<H> withValueMapping(AValueMappingDef<?,?,? super H> m) {
        valueMappings.add(m);
        return this;
    }

    public JavaBeanMapperBuilder<H> withObjectMapping(AObjectMappingDef<?,?,? super H> m) {
        objectMappings.add(m);
        return this;
    }

    @SuppressWarnings("unchecked")
    public JavaBeanMapperBuilder<H> withBeanMapping(JavaBeanMapping m) {
        objectMappings.add(m.build());

        if(m.getSourceClass() != m.getTargetClass()) {
            objectMappings.add(m.buildBackward());
        }
        return this;
    }


    public JavaBeanMapperBuilder<H> withLogger(AMapperLogger logger) {
        this.logger = logger;
        return this;
    }

    public JavaBeanMapperBuilder<H> withIdentifierExtractor(AIdentifierExtractor ie) {
        this.identifierExtractor = ie;
        return this;
    }

    public JavaBeanMapperBuilder<H> withHelperFactory(AFunction0<H, RuntimeException> helperFactory) {
        this.helperFactory = helperFactory;
        return this;
    }

    public JavaBeanMapperBuilder<H> withHelper(final H helper) {
        this.helperFactory = new AFunction0<H, RuntimeException>() {
            @Override public H apply() {
                return helper;
            }
        };
        return this;
    }

    public JavaBeanMapperBuilder<H> withContextExtractor(AContextExtractor contextExtractor) {
        this.contextExtractor = contextExtractor;
        return this;
    }

    public JavaBeanMapperBuilder<H> withPreProcessor(APreProcessor preProcessor) {
        this.preProcessors.add(preProcessor);
        return this;
    }

    public JavaBeanMapperBuilder<H> withPostProcessor(APostProcessor postProcessor) {
        this.postProcessors.add(postProcessor);
        return this;
    }

    public JavaBeanMapper build() {
        return build(false);
    }

    @SuppressWarnings("unchecked")
    public JavaBeanMapper build(boolean compile) {
        Collections.reverse(objectMappings);
        Collections.reverse(valueMappings);

        try {
            return new JavaBeanMapperImpl<H> (
                    objectMappings, valueMappings,
                    logger, helperFactory, identifierExtractor, contextExtractor,
                    preProcessors, postProcessors, compile
            );
        } catch (Exception exc) {
            return AMapperExceptionHandler.onError(exc, APath.EMPTY);
        }
    }
}
