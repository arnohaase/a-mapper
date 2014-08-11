package com.ajjpj.amapper.javabean;

import com.ajjpj.amapper.core.AValueMappingDef;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.javabean.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinValueMappingDefs;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.*;


/**
 * @author arno
 */
public class BuiltinValueMappingsTest {
    private AQualifiedSourceAndTargetType tpe(Class<?> cls) {
        return tpe(cls, cls);
    }
    private AQualifiedSourceAndTargetType tpe(Class<?> sourceCls, Class<?> targetCls) {
        return new AQualifiedSourceAndTargetType(JavaBeanTypes.create(sourceCls), AQualifier.NO_QUALIFIER, JavaBeanTypes.create(targetCls), AQualifier.NO_QUALIFIER);
    }

    @Test
    public void testString() throws Exception {
        assertTrue(BuiltinValueMappingDefs.StringMappingDef.canHandle(tpe(String.class, String.class)));

        assertFalse(BuiltinValueMappingDefs.StringMappingDef.canHandle(tpe(String.class, Object.class)));
        assertFalse(BuiltinValueMappingDefs.StringMappingDef.canHandle(tpe(Object.class, String.class)));
        assertFalse(BuiltinValueMappingDefs.StringMappingDef.canHandle(tpe(String.class, Date.class)));
        assertFalse(BuiltinValueMappingDefs.StringMappingDef.canHandle(tpe(Date.class,   String.class)));

        assertEquals("a",   BuiltinValueMappingDefs.StringMappingDef.map("a",   null, null, null));
        assertEquals("bcd", BuiltinValueMappingDefs.StringMappingDef.map("bcd", null, null, null));
        assertEquals(null,  BuiltinValueMappingDefs.StringMappingDef.map(null,  null, null, null));
    }

    //TODO test 'diff()'

    @Test
    public void testBoolean() throws Exception {
        assertTrue(BuiltinValueMappingDefs.BooleanMappingDef.canHandle(tpe(Boolean.class,  Boolean.class)));

        assertFalse(BuiltinValueMappingDefs.BooleanMappingDef.canHandle(tpe(Boolean.class, String.class)));
        assertFalse(BuiltinValueMappingDefs.BooleanMappingDef.canHandle(tpe(String.class, Boolean.class)));

        assertEquals(true,  BuiltinValueMappingDefs.BooleanMappingDef.map(true,  null, null, null));
        assertEquals(false, BuiltinValueMappingDefs.BooleanMappingDef.map(false, null, null, null));
        assertEquals(null,  BuiltinValueMappingDefs.BooleanMappingDef.map(null,  null, null, null));
    }

    @Test
    public void testChar() throws Exception {
        assertTrue(BuiltinValueMappingDefs.CharacterMappingDef.canHandle(tpe(Character.class,  Character.class)));

        assertFalse(BuiltinValueMappingDefs.CharacterMappingDef.canHandle(tpe(Character.class, String.class)));
        assertFalse(BuiltinValueMappingDefs.CharacterMappingDef.canHandle(tpe(String.class,    Character.class)));

        assertFalse(BuiltinValueMappingDefs.CharacterMappingDef.canHandle(tpe(Character.class, Integer.class)));
        assertFalse(BuiltinValueMappingDefs.CharacterMappingDef.canHandle(tpe(Integer.class,   Character.class)));

        assertFalse(BuiltinValueMappingDefs.CharacterMappingDef.canHandle(tpe(Character.class, Byte.class)));
        assertFalse(BuiltinValueMappingDefs.CharacterMappingDef.canHandle(tpe(Byte.class, Character.class)));

        assertEquals(Character.valueOf('a'), BuiltinValueMappingDefs.CharacterMappingDef.map('a', null, null, null));
        assertEquals(Character.valueOf('ß'), BuiltinValueMappingDefs.CharacterMappingDef.map('ß', null, null, null));

        assertEquals(null,                   BuiltinValueMappingDefs.CharacterMappingDef.map(null, null, null, null));
    }

    @Test
    public void testEnum() throws Exception {
        assertTrue(BuiltinValueMappingDefs.EnumMappingDef.canHandle(tpe(RetentionPolicy.class, RetentionPolicy.class)));
        assertTrue(BuiltinValueMappingDefs.EnumMappingDef.canHandle(tpe(ElementType.class, ElementType.class)));

        assertFalse(BuiltinValueMappingDefs.EnumMappingDef.canHandle(tpe(ElementType.class, RetentionPolicy.class)));

        assertEquals(RetentionPolicy.RUNTIME, BuiltinValueMappingDefs.EnumMappingDef.map(RetentionPolicy.RUNTIME, null, null, null));
        assertEquals(ElementType.CONSTRUCTOR, BuiltinValueMappingDefs.EnumMappingDef.map(ElementType.CONSTRUCTOR, null, null, null));
    }

    @Test
    public void testDate() throws Exception {
        assertTrue(BuiltinValueMappingDefs.DateMappingDef.canHandle(tpe(java.util.Date.class,  java.util.Date.class)));

        assertFalse(BuiltinValueMappingDefs.DateMappingDef.canHandle(tpe(java.util.Date.class, String.class)));
        assertFalse(BuiltinValueMappingDefs.DateMappingDef.canHandle(tpe(String.class, java.util.Date.class)));

        // no built-in support for java.sql.* classes that extends java.util.Date - implicit conversion is a snake pit of ugly
        //  surprises, e.g. differing precision etc.

        assertFalse(BuiltinValueMappingDefs.DateMappingDef.canHandle(tpe(java.sql.Date.class,  java.sql.Date.class)));
        assertFalse(BuiltinValueMappingDefs.DateMappingDef.canHandle(tpe(java.sql.Date.class,  java.util.Date.class)));
        assertFalse(BuiltinValueMappingDefs.DateMappingDef.canHandle(tpe(java.util.Date.class, java.sql.Date.class)));

        assertEquals(new Date(12345),  BuiltinValueMappingDefs.DateMappingDef.map(new Date(12345),  null, null, null));
        assertEquals(new Date(-12345), BuiltinValueMappingDefs.DateMappingDef.map(new Date(-12345), null, null, null));
        assertEquals(null, BuiltinValueMappingDefs.DateMappingDef.map(null, null, null, null));
    }

    @Test
    public void testLocale() throws Exception {
        assertTrue(BuiltinValueMappingDefs.LocaleMappingDef.canHandle(tpe(Locale.class, Locale.class)));

        assertFalse(BuiltinValueMappingDefs.LocaleMappingDef.canHandle(tpe(Locale.class, String.class)));
        assertFalse(BuiltinValueMappingDefs.LocaleMappingDef.canHandle(tpe(String.class, Locale.class)));

        assertEquals(Locale.CANADA,        BuiltinValueMappingDefs.LocaleMappingDef.map(Locale.CANADA,        null, null, null));
        assertEquals(Locale.CANADA_FRENCH, BuiltinValueMappingDefs.LocaleMappingDef.map(Locale.CANADA_FRENCH, null, null, null));
        assertEquals(Locale.GERMAN,        BuiltinValueMappingDefs.LocaleMappingDef.map(Locale.GERMAN,        null, null, null));
    }

    @Test
    public void testTimeZone() throws Exception {
        assertTrue(BuiltinValueMappingDefs.TimeZoneMappingDef.canHandle(tpe(TimeZone.class, TimeZone.class)));

        assertFalse(BuiltinValueMappingDefs.TimeZoneMappingDef.canHandle(tpe(TimeZone.class, String.class)));
        assertFalse(BuiltinValueMappingDefs.TimeZoneMappingDef.canHandle(tpe(String.class,   TimeZone.class)));

        assertEquals(TimeZone.getTimeZone("UTC"), BuiltinValueMappingDefs.TimeZoneMappingDef.map(TimeZone.getTimeZone("UTC"), null, null, null));
        assertEquals(TimeZone.getTimeZone("PST"), BuiltinValueMappingDefs.TimeZoneMappingDef.map(TimeZone.getTimeZone("PST"), null, null, null));
    }

    @Test
    public void testCurrency() throws Exception {
        assertTrue(BuiltinValueMappingDefs.CurrencyMappingDef.canHandle(tpe(Currency.class, Currency.class)));

        assertFalse(BuiltinValueMappingDefs.CurrencyMappingDef.canHandle(tpe(Currency.class, String.class)));
        assertFalse(BuiltinValueMappingDefs.CurrencyMappingDef.canHandle(tpe(String.class,   Currency.class)));

        assertEquals(Currency.getInstance("USD"), BuiltinValueMappingDefs.CurrencyMappingDef.map(Currency.getInstance("USD"), null, null, null));
        assertEquals(Currency.getInstance("EUR"), BuiltinValueMappingDefs.CurrencyMappingDef.map(Currency.getInstance("EUR"), null, null, null));
    }

    private void checkNumber(AValueMappingDef vm, Class<?> cls, Object expected) throws Exception {
        checkNumber(vm, cls, expected, (Number) expected);
    }

    @SuppressWarnings("unchecked")
    private void checkNumber(AValueMappingDef vm, Class<?> cls, Object expected, Number input) throws Exception {
        assertTrue(vm.canHandle(tpe(Byte.class,       cls)));
        assertTrue(vm.canHandle(tpe(Short.class,      cls)));
        assertTrue(vm.canHandle(tpe(Integer.class,    cls)));
        assertTrue(vm.canHandle(tpe(Long.class,       cls)));
        assertTrue(vm.canHandle(tpe(Float.class,      cls)));
        assertTrue(vm.canHandle(tpe(Double.class,     cls)));
        assertTrue(vm.canHandle(tpe(BigInteger.class, cls)));
        assertTrue(vm.canHandle(tpe(BigDecimal.class, cls)));
        assertTrue(vm.canHandle(tpe(Number.class,     cls)));

        assertFalse(vm.canHandle(tpe(cls, cls.equals(Integer.class) ? Long.class : Integer.class)));

        assertEquals(expected, vm.map(input.byteValue(),   null, null, null));
        assertEquals(expected, vm.map(input.shortValue(),  null, null, null));
        assertEquals(expected, vm.map(input.intValue(),    null, null, null));
        assertEquals(expected, vm.map(input.longValue(),   null, null, null));
        assertEquals(expected, vm.map(input.floatValue(),  null, null, null));
        assertEquals(expected, vm.map(input.doubleValue(), null, null, null));

        assertEquals(expected, vm.map(new BigInteger("" + input),     null, null, null));
        assertEquals(expected, vm.map(new BigDecimal("" + input),     null, null, null));

        assertEquals(null, vm.map(null, null, null, null));
    }

    @Test
    public void testByte() throws Exception {
        checkNumber(BuiltinValueMappingDefs.ByteMappingDef, Byte.class, (byte) 1);
        checkNumber(BuiltinValueMappingDefs.ByteMappingDef, Byte.class, (byte) 2);
        checkNumber(BuiltinValueMappingDefs.ByteMappingDef, Byte.class, (byte) -1);
    }

    @Test
    public void testShort() throws Exception {
        checkNumber(BuiltinValueMappingDefs.ShortMappingDef, Short.class, (short) 1);
        checkNumber(BuiltinValueMappingDefs.ShortMappingDef, Short.class, (short) 2);
        checkNumber(BuiltinValueMappingDefs.ShortMappingDef, Short.class, (short) -1);
    }

    @Test
    public void testInteger() throws Exception {
        checkNumber(BuiltinValueMappingDefs.IntegerMappingDef, Integer.class, 1);
        checkNumber(BuiltinValueMappingDefs.IntegerMappingDef, Integer.class, 2);
        checkNumber(BuiltinValueMappingDefs.IntegerMappingDef, Integer.class, -1);
    }

    @Test
    public void testLong() throws Exception {
        checkNumber(BuiltinValueMappingDefs.LongMappingDef, Long.class, (long) 1);
        checkNumber(BuiltinValueMappingDefs.LongMappingDef, Long.class, (long) 2);
        checkNumber(BuiltinValueMappingDefs.LongMappingDef, Long.class, (long) -1);
    }

    @Test
    public void testFloat() throws Exception {
        checkNumber(BuiltinValueMappingDefs.FloatMappingDef, Float.class, (float) 1, 1);
        checkNumber(BuiltinValueMappingDefs.FloatMappingDef, Float.class, (float) 2, 2);
        checkNumber(BuiltinValueMappingDefs.FloatMappingDef, Float.class, (float) -1, -1);
    }

    @Test
    public void testDouble() throws Exception {
        checkNumber(BuiltinValueMappingDefs.DoubleMappingDef, Double.class, (double) 1, 1);
        checkNumber(BuiltinValueMappingDefs.DoubleMappingDef, Double.class, (double) 2, 2);
        checkNumber(BuiltinValueMappingDefs.DoubleMappingDef, Double.class, (double) -1, -1);
    }

    @Test
    public void testBigInteger() throws Exception {
        checkNumber(BuiltinValueMappingDefs.BigIntegerMappingDef, BigInteger.class, new BigInteger("1"), 1);
        checkNumber(BuiltinValueMappingDefs.BigIntegerMappingDef, BigInteger.class, new BigInteger("2"), 2);
        checkNumber(BuiltinValueMappingDefs.BigIntegerMappingDef, BigInteger.class, new BigInteger("-1"), -1);

        final BigInteger veryBigInt     = new BigInteger("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
        final BigDecimal veryBigDecimal = new BigDecimal("12345678901234567890123456789012345678901234567890123456789012345678901234567890.123");
        assertEquals(veryBigInt, BuiltinValueMappingDefs.BigIntegerMappingDef.map(veryBigInt,     null, null, null));
        assertEquals(veryBigInt, BuiltinValueMappingDefs.BigIntegerMappingDef.map(veryBigDecimal, null, null, null));
    }

    @Test
    public void testBigDecimal() throws Exception {
        checkNumber(BuiltinValueMappingDefs.BigDecimalMappingDef, BigDecimal.class, new BigDecimal("1"), 1);
        checkNumber(BuiltinValueMappingDefs.BigDecimalMappingDef, BigDecimal.class, new BigDecimal("2"), 2);
        checkNumber(BuiltinValueMappingDefs.BigDecimalMappingDef, BigDecimal.class, new BigDecimal("-1"), -1);

        final BigInteger veryBigInt      = new BigInteger("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
        final BigDecimal veryBigDecimal0 = new BigDecimal("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
        final BigDecimal veryBigDecimal3 = new BigDecimal("12345678901234567890123456789012345678901234567890123456789012345678901234567890.123");

        assertEquals(veryBigDecimal0, BuiltinValueMappingDefs.BigDecimalMappingDef.map(veryBigInt,      null, null, null));
        assertEquals(veryBigDecimal3, BuiltinValueMappingDefs.BigDecimalMappingDef.map(veryBigDecimal3, null, null, null));
    }

    @Test
    public void testUuid() throws Exception {
        final JavaBeanMapper m = JavaBeanMapperBuilder.create ().build ();

        final UUID uuid = UUID.randomUUID ();
        final UUID mapped = m.map (uuid, UUID.class);
        assertSame (uuid, mapped);
    }
}

