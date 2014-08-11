package com.ajjpj.amapper.javabean.mappingdef;

import com.ajjpj.amapper.core.AValueMappingDef;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;


/**
 * @author arno
 */
public class BuiltinValueMappingDefs {
    public static final AValueMappingDef<String,     String,     Object> StringMappingDef     = new PassThroughValueMappingDef <> (String.class);
    public static final AValueMappingDef<Boolean,    Boolean,    Object> BooleanMappingDef    = new PassThroughValueMappingDef <> (Boolean.class);
    public static final AValueMappingDef<Character , Character,  Object> CharacterMappingDef  = new PassThroughValueMappingDef <> (Character.class);

    public static final AValueMappingDef<Date,     Date,     Object> DateMappingDef     = new PassThroughValueMappingDef <> (Date.class);
    public static final AValueMappingDef<Locale,   Locale,   Object> LocaleMappingDef   = new PassThroughValueMappingDef <> (Locale.class);
    public static final AValueMappingDef<TimeZone, TimeZone, Object> TimeZoneMappingDef = new PassThroughValueMappingDef <> (TimeZone.class);
    public static final AValueMappingDef<Currency, Currency, Object> CurrencyMappingDef = new PassThroughValueMappingDef <> (Currency.class);
    public static final AValueMappingDef<UUID,     UUID,     Object> UuidMappingDef     = new PassThroughValueMappingDef <> (UUID.class);

    @SuppressWarnings(value = {"raw", "unchecked"})
    public static final AValueMappingDef<Class<?>, Class<?>, Object> ClassMappingDef = new PassThroughValueMappingDef <Class<?>> ((Class) Class.class);

    @SuppressWarnings(value = {"raw", "unchecked"})
    public static final AValueMappingDef <Enum<?>, Enum<?>, Object> EnumMappingDef = new SubTypeCheckingPassThroughValueMappingDef<Enum<?>> ((Class) Enum.class);


    public static final FromNumberValueMappingDef<Byte> ByteMappingDef = new FromNumberValueMappingDef<Byte>(Byte.class) {
        @Override protected Byte fromNumber(Number n) { return n.byteValue(); }
    };
    public static final FromNumberValueMappingDef<Short> ShortMappingDef = new FromNumberValueMappingDef<Short>(Short.class) {
        @Override protected Short fromNumber(Number n) { return n.shortValue(); }
    };
    public static final FromNumberValueMappingDef<Integer> IntegerMappingDef = new FromNumberValueMappingDef<Integer>(Integer.class) {
        @Override protected Integer fromNumber(Number n) { return n.intValue(); }
    };
    public static final FromNumberValueMappingDef<Long> LongMappingDef = new FromNumberValueMappingDef<Long>(Long.class) {
        @Override protected Long fromNumber(Number n) { return n.longValue(); }
    };
    public static final FromNumberValueMappingDef<Float> FloatMappingDef = new FromNumberValueMappingDef<Float>(Float.class) {
        @Override protected Float fromNumber(Number n) { return n.floatValue(); }
    };
    public static final FromNumberValueMappingDef<Double> DoubleMappingDef = new FromNumberValueMappingDef<Double>(Double.class) {
        @Override protected Double fromNumber(Number n) { return n.doubleValue(); }
    };
    public static final FromNumberValueMappingDef<BigInteger> BigIntegerMappingDef = new FromNumberValueMappingDef<BigInteger> (BigInteger.class) {
        @Override protected BigInteger fromNumber(Number n) {
            if(n instanceof BigInteger) {
                return (BigInteger) n;
            }
            if(n instanceof BigDecimal) {
                return ((BigDecimal) n).toBigInteger();
            }
            return BigInteger.valueOf(n.longValue());
        }
    };
    public static final FromNumberValueMappingDef<BigDecimal> BigDecimalMappingDef = new FromNumberValueMappingDef<BigDecimal> (BigDecimal.class) {
        @Override protected BigDecimal fromNumber(Number n) {
            if(n instanceof BigDecimal) {
                return (BigDecimal) n;
            }
            if(n instanceof BigInteger) {
                return new BigDecimal((BigInteger)n);
            }
            if(n instanceof Double || n instanceof Float) {
                return new BigDecimal(n.doubleValue());
            }
            return BigDecimal.valueOf(n.longValue());
        }
    };
}
