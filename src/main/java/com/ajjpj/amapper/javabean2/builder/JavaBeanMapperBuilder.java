package com.ajjpj.amapper.javabean2.builder;

import com.ajjpj.amapper.core2.*;
import com.ajjpj.amapper.core2.exclog.AMapperLogger;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core2.tpe.CanHandleSourceAndTargetCache;
import com.ajjpj.amapper.javabean2.JavaBeanMapper;
import com.ajjpj.amapper.javabean2.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean2.SimpleJavaBeanMappingHelper;
import com.ajjpj.amapper.javabean2.impl.JavaBeanMapperImpl;
import com.ajjpj.amapper.javabean2.mappingdef.BuiltinValueMappingDefs;
import com.ajjpj.amapper.util.func.AFunction0;

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
            BuiltinValueMappingDefs.ClassMappingDef, BuiltinValueMappingDefs.EnumMappingDef,
            BuiltinValueMappingDefs.ByteMappingDef, BuiltinValueMappingDefs.ShortMappingDef, BuiltinValueMappingDefs.IntegerMappingDef, BuiltinValueMappingDefs.LongMappingDef,
            BuiltinValueMappingDefs.FloatMappingDef, BuiltinValueMappingDefs.DoubleMappingDef
    ));

    private AMapperLogger logger = AMapperLogger.defaultLogger();
    private AFunction0<H, Exception> helperFactory = new AFunction0<H, Exception>() {
        @SuppressWarnings("unchecked")
        @Override public H apply() throws Exception {
            return (H) SimpleJavaBeanMappingHelper.INSTANCE;
        }
    };
    private AIdentifierExtractor identifierExtractor = new AIdentifierExtractor() {
        @Override
        public Object uniqueIdentifier(Object o, AQualifiedSourceAndTargetType types) {
            return o.toString();
        }
    };
    private AContextExtractor contextExtractor = NoContextExtractor.INSTANCE;

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

        //TODO only if sourceCls != targetCls
        objectMappings.add(m.buildBackward());
        return this;
    }


    public JavaBeanMapperBuilder withLogger(AMapperLogger logger) {
        this.logger = logger;
        return this;
    }

    public JavaBeanMapperBuilder<H> withHelperFactory(AFunction0<H, Exception> helperFactory) {
        this.helperFactory = helperFactory;
        return this;
    }

    public JavaBeanMapperBuilder<H> withHelper(final H helper) {
        this.helperFactory = new AFunction0<H, Exception>() {
            @Override public H apply() throws Exception {
                return helper;
            }
        };
        return this;
    }

    public JavaBeanMapperBuilder<H> withContextExtractor(AContextExtractor contextExtractor) {
        this.contextExtractor = contextExtractor;
        return this;
    }

    @SuppressWarnings("unchecked")
    public JavaBeanMapper build() {
        Collections.reverse(objectMappings);
        Collections.reverse(valueMappings);

        return new JavaBeanMapperImpl<H>(
                new CanHandleSourceAndTargetCache<AObjectMappingDef<?,?,? super H>, AObjectMappingDef<Object,Object,H>> ("No object mapping found for ", objectMappings),
                new CanHandleSourceAndTargetCache<AValueMappingDef <?,?,? super H>, AValueMappingDef <Object,Object,H>> ("No value mapping found for ",  valueMappings),
                logger, helperFactory, identifierExtractor, contextExtractor,
                new CanHandleSourceAndTargetCache<APreProcessor, APreProcessor>("no preprocessor for ", preProcessors),
                new CanHandleSourceAndTargetCache<APostProcessor, APostProcessor>("no postprocessor for ", postProcessors)
        );
    }
}
