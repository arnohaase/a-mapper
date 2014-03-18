package com.ajjpj.amapper.core;

import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.amapper.classes.ClassRequiringContext;
import com.ajjpj.amapper.classes.ClassWithContext;
import com.ajjpj.amapper.classes.PriceClass;
import com.ajjpj.amapper.classes.TestCurrencyProvider;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.JavaBeanMapper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.mappingdef.AbstractJavaBeanValueMappingDef;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author arno
 */
public class ContextTest {
    @Test
    public void testContext () throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withBeanMapping(JavaBeanMapping.create(ClassWithContext.class, ClassWithContext.class).withMatchingPropsMappings())
            .withBeanMapping(JavaBeanMapping.create(ClassRequiringContext.class, ClassRequiringContext.class)
                    .withMatchingPropsMappings()
                    .removeMappingForTargetProp("price")
                    .removeMappingForSourceProp("amount")
                    .addOneWayMapping("amount", Double.class, "price", PriceClass.class)
            )
            .withValueMapping(new AbstractJavaBeanValueMappingDef<Double, PriceClass, Object>(Double.class, PriceClass.class) {
                @Override
                public PriceClass map(Double sourceValue, AQualifiedSourceAndTargetType types, AMapperWorker<?> worker, AMap<String, Object> context) {
                    final TestCurrencyProvider tcp = (TestCurrencyProvider) context.get(TestCurrencyProvider.class.getName()).get();
                    return new PriceClass(sourceValue, tcp.getCurrency());
                }

                @Override
                public void diff(ADiffBuilder diff, Double sourceOld, Double sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
                }
            })
            .build();

        final ClassWithContext o = new ClassWithContext ();
        o.setCurrency ("USD");
        o.setRequiringContext (new ClassRequiringContext ());
        o.getRequiringContext ().setAmount (2.0);

        final ClassWithContext mapped = mapper.map (o, ClassWithContext.class);
        assertEquals ("2.0 USD", mapped.getRequiringContext ().getPrice ().toString ());
    }
}
