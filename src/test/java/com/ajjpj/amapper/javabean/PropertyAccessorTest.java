package com.ajjpj.amapper.javabean;

import com.ajjpj.afoundation.collection.immutable.AOption;
import com.ajjpj.amapper.classes.PartOfPropPath;
import com.ajjpj.amapper.classes.WithProperties;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.javabean.builder.ABeanExpressionParser;
import com.ajjpj.amapper.javabean.builder.qualifier.NoQualifierExtractor;
import com.ajjpj.amapper.javabean.propbased.accessors.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * @author arno
 */
public class PropertyAccessorTest {

    public void checkSimpleAccessor(APropertyAccessor acc) throws Exception {
        checkSimpleAccessor(acc, "propName");
    }

    public void checkSimpleAccessor(APropertyAccessor acc, String propName) throws Exception {
        checkSimpleAccessor(acc, propName, AOption.<APropertyAccessor>none());
    }

    public void checkSimpleAccessor(APropertyAccessor acc, String propName, AOption<APropertyAccessor> auxSetter) throws Exception {
        assertEquals(propName, acc.getName());

        final WithProperties o = new WithProperties();
        o.inner.inner = o;

        if(auxSetter.isDefined()) {
            assertEquals(false, acc.isWritable());

            assertEquals(null, acc.get(o));
            auxSetter.get().set(o, "xyz");
            assertEquals("xyz", acc.get(o));
        }
        else {
            assertEquals(true, acc.isWritable());

            assertEquals(null, acc.get(o));
            acc.set(o, "xyz");
            assertEquals("xyz", acc.get(o));
        }
    }

    public void checkPathAccessor(APropertyAccessor acc, boolean firstNullSafe, boolean secondNullSafe, boolean lastNullSafe) throws Exception {
        checkSimpleAccessor(acc, acc.getName());

        final WithProperties o = new WithProperties();
        if(lastNullSafe) {
            assertEquals(null, acc.get(o));
        }
        else {
            try {
                acc.get(o);
                fail("exception expected");
            }
            catch (Exception exc) { //
            }
        }

        o.inner = null;
        if(secondNullSafe) {
            assertEquals(null, acc.get(o));
        }
        else {
            try {
                acc.get(o);
                fail("exception expected");
            }
            catch (Exception exc) { //
            }
        }

        if(firstNullSafe) {
            assertEquals(null, acc.get(null));
        }
        else {
            try {
                acc.get(null);
                fail("exception expected");
            }
            catch (Exception exc) { //
            }
        }
    }

    @Test
    public void testField() throws Exception {
        checkSimpleAccessor(new AFieldBasedPropertyAccessor("propName", WithProperties.class.getDeclaredField("theString"), false, JavaBeanTypes.create(String.class), AQualifier.NO_QUALIFIER, AQualifier.NO_QUALIFIER));
    }

    @Test
    public void testMethod() throws Exception {
        checkSimpleAccessor(new AMethodBasedPropertyAccessor("propName", WithProperties.class.getDeclaredMethod("getAbc"), WithProperties.class.getDeclaredMethod("setAbc", String.class), false, JavaBeanTypes.create(String.class), AQualifier.NO_QUALIFIER, AQualifier.NO_QUALIFIER));
        assertEquals(false, new AMethodBasedPropertyAccessor("a", WithProperties.class.getDeclaredMethod("getAbc"), null, true, JavaBeanTypes.create(String.class), AQualifier.NO_QUALIFIER, AQualifier.NO_QUALIFIER).isWritable());
    }

    @Test
    public void testOgnl() throws Exception {
        checkSimpleAccessor(new AOgnlPropertyAccessor("propName", "xyz", WithProperties.class, true, JavaBeanTypes.create(String.class), AQualifier.NO_QUALIFIER, AQualifier.NO_QUALIFIER));

        assertEquals(true,  new AOgnlPropertyAccessor("propName", "xyz",      WithProperties.class, true, JavaBeanTypes.create(String.class), AQualifier.NO_QUALIFIER, AQualifier.NO_QUALIFIER).isWritable());
        assertEquals(false, new AOgnlPropertyAccessor("readOnly", "readOnly", WithProperties.class, true, JavaBeanTypes.create(String.class), AQualifier.NO_QUALIFIER, AQualifier.NO_QUALIFIER).isWritable());
    }

    private void checkMethodPath(boolean firstNullSafe, boolean secondNullSafe, boolean finalNullSafe) throws Exception {
        final List<AMethodPathBasedPropertyAccessor.Step> steps = new ArrayList<AMethodPathBasedPropertyAccessor.Step>();
        steps.add(new AMethodPathBasedPropertyAccessor.Step(WithProperties.class.getMethod("getOther"), firstNullSafe));
        steps.add(new AMethodPathBasedPropertyAccessor.Step(PartOfPropPath.class.getMethod("getWithProperties"), secondNullSafe));

        final APropertyAccessor acc = new AMethodPathBasedPropertyAccessor("propName", steps, WithProperties.class.getMethod("getAbc"), WithProperties.class.getMethod("setAbc", String.class), finalNullSafe, false, JavaBeanTypes.create(String.class), AQualifier.NO_QUALIFIER, AQualifier.NO_QUALIFIER);

        checkPathAccessor(acc, firstNullSafe, secondNullSafe, finalNullSafe);
    }

    //TODO test readable / writable

    @Test
    public void testMethodPath() throws Exception {
        checkMethodPath(false, false, false);
        checkMethodPath(true,  false, false);
        checkMethodPath(false, true,  false);
        checkMethodPath(false, false, true); //TODO test null safety of setter
    }

    @Test
    public void testParsedMethod() throws Exception {
        final APropertyAccessor acc = new ABeanExpressionParser(NoQualifierExtractor.INSTANCE).parse(WithProperties.class, "theString", JavaBeanTypes.create(String.class), false);
        checkSimpleAccessor(acc, "theString");
    }

    @Test
    public void testParsedOgnl() throws Exception {
        final APropertyAccessor acc = new ABeanExpressionParser(NoQualifierExtractor.INSTANCE).parse(WithProperties.class, "1>0?theString:null", JavaBeanTypes.create(String.class), false);
        checkSimpleAccessor(acc, "1>0?theString:null");
    }

    @Test
    public void testParsedPathAsOgnl() throws Exception {
        final APropertyAccessor acc = new ABeanExpressionParser(NoQualifierExtractor.INSTANCE).parse(WithProperties.class, "theString.substring(0)", JavaBeanTypes.create(String.class), false);
        assertTrue(acc instanceof AOgnlPropertyAccessor);
    }

    @Test
    public void testParsedPath() throws Exception {
        final APropertyAccessor acc = new ABeanExpressionParser(NoQualifierExtractor.INSTANCE).parse(WithProperties.class, "other.withProperties.theString", JavaBeanTypes.create(String.class), false);
        assertTrue(acc instanceof AMethodPathBasedPropertyAccessor);
        checkPathAccessor(acc, false, false, false);
    }

    @Test
    public void testParsedPathFirstNullSafe() throws Exception {
        final APropertyAccessor acc = new ABeanExpressionParser(NoQualifierExtractor.INSTANCE).parse(WithProperties.class, "?other.withProperties.theString", JavaBeanTypes.create(String.class), false);
        assertTrue(acc instanceof AMethodPathBasedPropertyAccessor);
        checkPathAccessor(acc, true, false, false);
    }

    @Test
    public void testParsedPathSecondNullSafe() throws Exception {
        final APropertyAccessor acc = new ABeanExpressionParser(NoQualifierExtractor.INSTANCE).parse(WithProperties.class, "other.?withProperties.theString", JavaBeanTypes.create(String.class), false);
        assertTrue(acc instanceof AMethodPathBasedPropertyAccessor);
        checkPathAccessor(acc, false, true, false);
    }

    @Test
    public void testParsedPathLastNullSafe() throws Exception {
        final APropertyAccessor acc = new ABeanExpressionParser(NoQualifierExtractor.INSTANCE).parse(WithProperties.class, "other.withProperties.?theString", JavaBeanTypes.create(String.class), false);
        assertTrue(acc instanceof AMethodPathBasedPropertyAccessor);
        checkPathAccessor(acc, false, false, true);
    }
}
