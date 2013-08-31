package com.ajjpj.amapper.javabean.japi;

import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.AQualifier;
import com.ajjpj.amapper.core.AType;
import com.ajjpj.amapper.javabean.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.japi.classes.WithQualifiers;
import com.ajjpj.amapper.javabean.japi.classes.WithoutQualifiers;
import org.junit.Test;
import scala.collection.immutable.Map;

import static org.junit.Assert.*;


/**
 * @author arno
 */
public class QualifierTest {
    final AbstractValueMappingDef<String, String, JavaBeanMappingHelper> fromQualifier = new AbstractValueMappingDef<String, String, JavaBeanMappingHelper>(String.class, String.class) {
        @Override
        public boolean canHandle(AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) {
            System.out.println("checking fromQualifier with " + sourceQualifier);
            if (!super.canHandle(sourceType, sourceQualifier, targetType, targetQualifier)) {
                return false;
            }
            return sourceQualifier.get("qualifier-test").isDefined();
        }

        @Override
        public String map(String sourceValue, AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> context) {
            return sourceValue + " from qualifier";
        }
    };

    final AbstractValueMappingDef<String, String, JavaBeanMappingHelper> toQualifier = new AbstractValueMappingDef<String, String, JavaBeanMappingHelper>(String.class, String.class) {
        @Override
        public boolean canHandle(AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) {
            if (!super.canHandle(sourceType, sourceQualifier, targetType, targetQualifier)) {
                return false;
            }
            return targetQualifier.get("qualifier-test").isDefined();
        }

        @Override
        public String map(String sourceValue, AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier, AMapperWorker<? extends JavaBeanMappingHelper> worker, Map<String, Object> context) {
            return sourceValue + " to qualifier " + targetQualifier.get("qualifier-test").get();
        }
    };

    final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
            .withValueMapping(fromQualifier)
            .withValueMapping(toQualifier)
            .withBeanMapping(JavaBeanMapping.create(WithQualifiers.class, WithoutQualifiers.class))
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
