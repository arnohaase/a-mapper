package com.ajjpj.amapper.examples.context;

import com.ajjpj.amapper.javabean.JavaBeanMapper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapping;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Currency;

/**
 * @author arno
 */
public class _ContextTest {
    @Test
    public void testContext() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withValueMapping(new DoubleToMoneyMapping())
                .withBeanMapping(JavaBeanMapping.create(NormalizedInvoice.class, VerboseInvoice.class).withMatchingPropsMappings())
                .withBeanMapping(JavaBeanMapping.create(NormalizedPosition.class, VerbosePosition.class).withMatchingPropsMappings())
                .build();

        final NormalizedInvoice source = new NormalizedInvoice();
        source.setCurrency(Currency.getInstance("USD"));
        source.getPosition().setAmount(100);

        final VerboseInvoice mapped = mapper.map(source, VerboseInvoice.class);
        assertEquals("USD", mapped.getPosition().getAmount().getCurrency().getCurrencyCode());
        assertEquals(100.0, mapped.getPosition().getAmount().getAmount(), .0000000001);
    }
}
