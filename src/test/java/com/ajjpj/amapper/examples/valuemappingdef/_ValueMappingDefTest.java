package com.ajjpj.amapper.examples.valuemappingdef;

import com.ajjpj.amapper.javabean.JavaBeanMapper;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;


/**
 * @author arno
 */
public class _ValueMappingDefTest {
    @Test
    public void testValueMapping() throws Exception {
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create()
                .withValueMapping(new DateToLongValueMappingDef())
                .build();

        final Date d = new Date(12345);

        assertEquals(Long.valueOf(12345), mapper.map(d, Long.class));
    }
}
