package com.ajjpj.amapper.examples.doubleToBigDecimalQualifier;


import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import com.ajjpj.amapper.javabean.japi.JavaBeanMapper;
import com.ajjpj.amapper.javabean.japi.JavaBeanMapperBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

import java.math.BigDecimal;

/**
 * @author arno
 */
public class _QualifierTest {
    @Test
    public void testQualifier() {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withValueMapping(new DoubleToBigDecimalForCurrencyMappingDef())
                .withValueMapping(new DoubleToBigDecimalWithRoundingMappingDef())
                .withBeanMapping(JavaBeanMapping.create(ClassWithDouble.class, ClassWithBigDecimal.class)
                        .addMapping("amount", Double.class, "amount", BigDecimal.class)
                        .addMapping("withOneFractDigit", Double.class, "withOneFractDigit", BigDecimal.class)
//                        .addMapping("unqualified", Double.class, "unqualified", BigDecimal.class)
                )
                .build();

        final ClassWithDouble source = new ClassWithDouble();
        source.setAmount(123.4567);
        source.setWithOneFractDigit(1.2345);

        final ClassWithBigDecimal target = mapper.map(source, ClassWithBigDecimal.class);
        assertEquals(new BigDecimal("123.46"), target.getAmount());
        assertEquals(new BigDecimal("1.2"),    target.getWithOneFractDigit());
    }
}
