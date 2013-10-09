package com.ajjpj.amapper.javabean2;

import com.ajjpj.amapper.core2.AValueMappingDef;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core2.tpe.AQualifier;
import com.ajjpj.amapper.javabean2.mappingdef.BuiltinValueMappingDefs;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

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
    public void testString() {
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
    public void testBoolean() {
        assertTrue(BuiltinValueMappingDefs.BooleanMappingDef.canHandle(tpe(Boolean.class,  Boolean.class)));

        assertFalse(BuiltinValueMappingDefs.BooleanMappingDef.canHandle(tpe(Boolean.class, String.class)));
        assertFalse(BuiltinValueMappingDefs.BooleanMappingDef.canHandle(tpe(String.class, Boolean.class)));

        assertEquals(true,  BuiltinValueMappingDefs.BooleanMappingDef.map(true,  null, null, null));
        assertEquals(false, BuiltinValueMappingDefs.BooleanMappingDef.map(false, null, null, null));
        assertEquals(null,  BuiltinValueMappingDefs.BooleanMappingDef.map(null,  null, null, null));
    }

    @Test
    public void testChar() {
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
    public void testEnum() {
        assertTrue (BuiltinValueMappingDefs.EnumMappingDef.canHandle(tpe(RetentionPolicy.class, RetentionPolicy.class)));
        assertTrue(BuiltinValueMappingDefs.EnumMappingDef.canHandle(tpe(ElementType.class, ElementType.class)));

        assertFalse(BuiltinValueMappingDefs.EnumMappingDef.canHandle(tpe(ElementType.class, RetentionPolicy.class)));

        assertEquals(RetentionPolicy.RUNTIME, BuiltinValueMappingDefs.EnumMappingDef.map(RetentionPolicy.RUNTIME, null, null, null));
        assertEquals(ElementType.CONSTRUCTOR, BuiltinValueMappingDefs.EnumMappingDef.map(ElementType.CONSTRUCTOR, null, null, null));
    }

    @Test
    public void testDate() {
        fail("todo");
    }

    @Test
    public void testLocale() {
        fail("todo");
    }

    @Test
    public void testTimeZone() {
        fail("todo");
    }

    @Test
    public void testCurrency() {
        fail("todo");
    }

    private void checkNumber(AValueMappingDef vm, Class<?> cls, Object expected) {
        checkNumber(vm, cls, expected, (Number) expected);
    }

    @SuppressWarnings("unchecked")
    private void checkNumber(AValueMappingDef vm, Class<?> cls, Object expected, Number input) {
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
    public void testByte() {
        checkNumber(BuiltinValueMappingDefs.ByteMappingDef, Byte.class, (byte) 1);
        checkNumber(BuiltinValueMappingDefs.ByteMappingDef, Byte.class, (byte) 2);
        checkNumber(BuiltinValueMappingDefs.ByteMappingDef, Byte.class, (byte) -1);
    }

    @Test
    public void testShort() {
        checkNumber(BuiltinValueMappingDefs.ShortMappingDef, Short.class, (short) 1);
        checkNumber(BuiltinValueMappingDefs.ShortMappingDef, Short.class, (short) 2);
        checkNumber(BuiltinValueMappingDefs.ShortMappingDef, Short.class, (short) -1);
    }

    @Test
    public void testInteger() {
        checkNumber(BuiltinValueMappingDefs.IntegerMappingDef, Integer.class, 1);
        checkNumber(BuiltinValueMappingDefs.IntegerMappingDef, Integer.class, 2);
        checkNumber(BuiltinValueMappingDefs.IntegerMappingDef, Integer.class, -1);
    }

    @Test
    public void testLong() {
        checkNumber(BuiltinValueMappingDefs.LongMappingDef, Long.class, (long) 1);
        checkNumber(BuiltinValueMappingDefs.LongMappingDef, Long.class, (long) 2);
        checkNumber(BuiltinValueMappingDefs.LongMappingDef, Long.class, (long) -1);
    }

    @Test
    public void testFloat() {
        checkNumber(BuiltinValueMappingDefs.FloatMappingDef, Float.class, (float) 1, 1);
        checkNumber(BuiltinValueMappingDefs.FloatMappingDef, Float.class, (float) 2, 2);
        checkNumber(BuiltinValueMappingDefs.FloatMappingDef, Float.class, (float) -1, -1);
    }

    @Test
    public void testDouble() {
        checkNumber(BuiltinValueMappingDefs.DoubleMappingDef, Double.class, (double) 1, 1);
        checkNumber(BuiltinValueMappingDefs.DoubleMappingDef, Double.class, (double) 2, 2);
        checkNumber(BuiltinValueMappingDefs.DoubleMappingDef, Double.class, (double) -1, -1);
    }

    @Test
    public void testBigInteger() {
        checkNumber(BuiltinValueMappingDefs.BigIntegerMappingDef, BigInteger.class, new BigInteger("1"), 1);
        checkNumber(BuiltinValueMappingDefs.BigIntegerMappingDef, BigInteger.class, new BigInteger("2"), 2);
        checkNumber(BuiltinValueMappingDefs.BigIntegerMappingDef, BigInteger.class, new BigInteger("-1"), -1);

        final BigInteger veryBigInt     = new BigInteger("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
        final BigDecimal veryBigDecimal = new BigDecimal("12345678901234567890123456789012345678901234567890123456789012345678901234567890.123");
        assertEquals(veryBigInt, BuiltinValueMappingDefs.BigIntegerMappingDef.map(veryBigInt,     null, null, null));
        assertEquals(veryBigInt, BuiltinValueMappingDefs.BigIntegerMappingDef.map(veryBigDecimal, null, null, null));
    }

    @Test
    public void testBigDecimal() {
        checkNumber(BuiltinValueMappingDefs.BigDecimalMappingDef, BigDecimal.class, new BigDecimal("1"), 1);
        checkNumber(BuiltinValueMappingDefs.BigDecimalMappingDef, BigDecimal.class, new BigDecimal("2"), 2);
        checkNumber(BuiltinValueMappingDefs.BigDecimalMappingDef, BigDecimal.class, new BigDecimal("-1"), -1);

        final BigInteger veryBigInt      = new BigInteger("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
        final BigDecimal veryBigDecimal0 = new BigDecimal("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
        final BigDecimal veryBigDecimal3 = new BigDecimal("12345678901234567890123456789012345678901234567890123456789012345678901234567890.123");

        assertEquals(veryBigDecimal0, BuiltinValueMappingDefs.BigDecimalMappingDef.map(veryBigInt,      null, null, null));
        assertEquals(veryBigDecimal3, BuiltinValueMappingDefs.BigDecimalMappingDef.map(veryBigDecimal3, null, null, null));
    }
}

