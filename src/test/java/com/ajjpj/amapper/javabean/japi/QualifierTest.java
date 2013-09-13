package com.ajjpj.amapper.javabean.japi;

import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.QualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.japi.classes.WithQualifiers;
import com.ajjpj.amapper.javabean.japi.classes.WithoutQualifiers;
import org.junit.Test;
import scala.collection.immutable.Map;

import static org.junit.Assert.assertEquals;


/**
 * @author arno
 */
public class QualifierTest {
    final AbstractValueMappingDef<String, String, JavaBeanMappingHelper> fromQualifier = new AbstractValueMappingDef<String, String, JavaBeanMappingHelper>(String.class, String.class) {
        @Override
        public boolean canHandle(QualifiedSourceAndTargetType types) {
            if (!super.canHandle(types)) {
                return false;
            }
            return types.sourceQualifier().get("qualifier-test").isDefined();
        }

        @Override
        public String map(String sourceValue, QualifiedSourceAndTargetType types, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> context) {
            return sourceValue + " from qualifier";
        }
    };

    final AbstractValueMappingDef<String, String, JavaBeanMappingHelper> toQualifier = new AbstractValueMappingDef<String, String, JavaBeanMappingHelper>(String.class, String.class) {
        @Override
        public boolean canHandle(QualifiedSourceAndTargetType types) {
            if (!super.canHandle(types)) {
                return false;
            }
            return types.targetQualifier().get("qualifier-test").isDefined();
        }

        @Override
        public String map(String sourceValue, QualifiedSourceAndTargetType types, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> context) {
            return sourceValue + " to qualifier " + types.targetQualifier().get("qualifier-test").get();
        }
    };

    final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withValueMapping(fromQualifier)
            .withValueMapping(toQualifier)
            .withBeanMapping(JavaBeanMapping.create(WithQualifiers.class, WithoutQualifiers.class).withMatchingPropsMappings())
            .build();

    @Test
    public void testFromQualifiers() {
        final WithQualifiers orig = new WithQualifiers();
        orig.setS1("a");
        orig.setS2("b");

        final WithoutQualifiers mapped = mapper.map(orig, WithoutQualifiers.class);

        assertEquals("a from qualifier", mapped.getS1());
        assertEquals("b", mapped.getS2());
    }

    @Test
    public void testToQualifiers() {
        final WithoutQualifiers orig = new WithoutQualifiers();
        orig.setS1("a");
        orig.setS2("b");

        final WithQualifiers mapped = mapper.map(orig, WithQualifiers.class);

        assertEquals("a", mapped.getS1());
        assertEquals("b to qualifier xyz", mapped.getS2());
    }
}
