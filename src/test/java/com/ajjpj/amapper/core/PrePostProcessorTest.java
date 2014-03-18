package com.ajjpj.amapper.core;

import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.abase.function.AFunction0;
import com.ajjpj.amapper.AMapper;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.exclog.AMapperLogger;
import com.ajjpj.amapper.core.impl.AMapperImpl;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.core.tpe.AType;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinValueMappingDefs;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.junit.Assert.*;

/**
 * @author arno
 */
public class PrePostProcessorTest {
    public static class DataClass {
        public String x = "x";
        public String y = "unmapped";
        public int z = 1;
    }


    final AType type1 = new AType() { @Override public String getName() { return "type1"; } };
    final AType type2 = new AType() { @Override public String getName() { return "type2"; } };
    final AType type3 = new AType() { @Override public String getName() { return "type3"; } };
    final AType type4 = new AType() { @Override public String getName() { return "type4"; } };

    final List<AValueMappingDef<?,?,? super Object>> valueMappingDefs = new ArrayList<AValueMappingDef<?,?,? super Object>> ();
    {
        valueMappingDefs.addAll(Arrays.asList(BuiltinValueMappingDefs.StringMappingDef, BuiltinValueMappingDefs.IntegerMappingDef));
    }

    final AObjectMappingDef<Object,Object,Object> objectMapping = new AObjectMappingDef<Object, Object, Object>() {
        @Override public boolean isCacheable() {
            return true;
        }

        @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
            return true;
        }

        @Override public Object map(Object source, Object target, AQualifiedSourceAndTargetType types, AMapperWorker<?> worker, AMap<String, Object> context, APath path) throws Exception {
            ((DataClass)source).y = "mapped";
            return source;
        }

        @Override
        public void diff(ADiffBuilder diff, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
        }
    };

    final List<AObjectMappingDef<?, ?, ? super Object>> objectMappingDefs = new ArrayList<AObjectMappingDef<?, ?, ? super Object>>();
    {
        objectMappingDefs.add(objectMapping);
    }

    final APreProcessor pre = new APreProcessor() {
        @Override
        public boolean canHandle(AQualifiedSourceAndTargetType types) {
            return types.targetType == type1 || types.targetType == type2;
        }

        @Override
        public <T> AOption<T> preProcess(T o, AQualifiedSourceAndTargetType qt) {
            if(qt.sourceType == type1) {
                ((DataClass)o).x = "type1";
                return AOption.some(o);
            }
            if(qt.sourceType == type2) {
                ((DataClass)o).x = "type2";
                return AOption.some(o);
            }
            return AOption.none();
        }
    };

    final APostProcessor post = new APostProcessor() {
        @Override
        public boolean canHandle(AQualifiedSourceAndTargetType types) {
            return types.targetType == type1 || types.targetType == type3;
        }

        @Override
        public <T> T postProcess(T o, AQualifiedSourceAndTargetType qt) {
            if(qt.sourceType == type1) {
                ((DataClass)o).z += 1;
            }
            return o;
        }
    };

    final AFunction0<Object, RuntimeException> helperFactory = new AFunction0<Object, RuntimeException>() {
        @Override public Object apply() throws RuntimeException {
            return null;
        }
    };

    final AIdentifierExtractor ie = new AIdentifierExtractor() {
        @Override public Object uniqueIdentifier(Object o, AQualifiedSourceAndTargetType types) {
            return String.valueOf(o);
        }
    };

    final AMapper mapper = new AMapperImpl<Object>(objectMappingDefs, valueMappingDefs, AMapperLogger.StdOut, helperFactory, ie, NoContextExtractor.INSTANCE, Arrays.asList(pre), Arrays.asList(post));

    @Test
    public void testPreAndPostProcessor() throws Exception {
        final DataClass orig = new DataClass();
        assertSame(orig, mapper.map(orig, type1, AQualifier.NO_QUALIFIER, null, type1, AQualifier.NO_QUALIFIER).get());

        assertEquals("type1", orig.x);
        assertEquals("mapped", orig.y);
        assertEquals(2, orig.z);
    }

    @Test
    public void testSimplePostprocessor() throws Exception {
        final DataClass orig = new DataClass();
        assertSame(orig, mapper.map(orig, type1, AQualifier.NO_QUALIFIER, null, type3, AQualifier.NO_QUALIFIER).get());

        assertEquals("x", orig.x);
        assertEquals("mapped", orig.y);
        assertEquals(2, orig.z);
    }

    @Test
    public void testSimplePreprocessor() throws Exception {
        final DataClass orig = new DataClass();
        assertSame(orig, mapper.map(orig, type1, AQualifier.NO_QUALIFIER, null, type2, AQualifier.NO_QUALIFIER).get());

        assertEquals("type1", orig.x);
        assertEquals("mapped", orig.y);
        assertEquals(1, orig.z);
    }

    @Test
    public void testSimplePreprocessor2() throws Exception {
        final DataClass orig = new DataClass();
        assertSame(orig, mapper.map(orig, type2, AQualifier.NO_QUALIFIER, null, type2, AQualifier.NO_QUALIFIER).get());

        assertEquals("type2", orig.x);
        assertEquals("mapped", orig.y);
        assertEquals(1, orig.z);
    }

    @Test
    public void testNoPreprocessor() throws Exception {
        final DataClass orig = new DataClass();
        assertSame(orig, mapper.map(orig, type1, AQualifier.NO_QUALIFIER, null, type4, AQualifier.NO_QUALIFIER).get());

        assertEquals("x", orig.x);
        assertEquals("mapped", orig.y);
        assertEquals(1, orig.z);
    }

    @Test
    public void testPreprocessorCausesSkip() throws Exception {
        final DataClass orig = new DataClass();
        final DataClass oldTarget = new DataClass();

        final AOption<Object> mapped = mapper.map(orig, type3, AQualifier.NO_QUALIFIER, oldTarget, type1, AQualifier.NO_QUALIFIER);
        assertFalse (mapped.isDefined());
    }

    @Test
    public void testChainedPreProcessors() throws Exception {
        class MockPreProcessor implements APreProcessor {
            private final boolean isActive;
            public int count = 0;
            public boolean passThrough = true;

            MockPreProcessor(boolean isActive) {
                this.isActive = isActive;
            }

            @Override public <T> AOption<T> preProcess(T o, AQualifiedSourceAndTargetType qt) {
                count += 1;
                return passThrough ? AOption.some(o) : AOption.<T>none();
            }

            @Override public boolean canHandle(AQualifiedSourceAndTargetType types) throws Exception {
                return isActive;
            }
        }

        final MockPreProcessor pre1 = new MockPreProcessor(true);
        final MockPreProcessor pre2 = new MockPreProcessor(false);
        final MockPreProcessor pre3 = new MockPreProcessor(true);

        final AMapper mapper = new AMapperImpl(objectMappingDefs, Collections.emptyList(), AMapperLogger.StdOut, helperFactory, ie, NoContextExtractor.INSTANCE, Arrays.asList(pre1, pre2, pre3), Collections.emptyList());

        assertEquals(true, mapper.map(new DataClass(), type1, AQualifier.NO_QUALIFIER, null, type1, AQualifier.NO_QUALIFIER).isDefined());
        assertEquals(1, pre1.count);
        assertEquals(0, pre2.count);
        assertEquals(1, pre3.count);

        pre1.passThrough = false;
        assertEquals(false, mapper.map(new DataClass(), type1, AQualifier.NO_QUALIFIER, null, type1, AQualifier.NO_QUALIFIER).isDefined());
        assertEquals(2, pre1.count);
        assertEquals(0, pre2.count);
        assertEquals(1, pre3.count);

        pre1.passThrough = true;
        pre3.passThrough = false;
        assertEquals(false, mapper.map(new DataClass(), type1, AQualifier.NO_QUALIFIER, null, type1, AQualifier.NO_QUALIFIER).isDefined());
        assertEquals(3, pre1.count);
        assertEquals(0, pre2.count);
        assertEquals(2, pre3.count);
    }

    @Test
    public void testChainedPostProcessors() throws Exception {
        class MockPostProcessor implements APostProcessor {
            private final boolean isActive;
            public boolean passThrough = true;
            public int count = 0;

            MockPostProcessor(boolean isActive) {
                this.isActive = isActive;
            }

            @Override public <T> T postProcess(T o, AQualifiedSourceAndTargetType qt) {
                count += 1;
                return passThrough ? o : null;
            }

            @Override public boolean canHandle(AQualifiedSourceAndTargetType types) throws Exception {
                return isActive;
            }
        }

        final MockPostProcessor post1 = new MockPostProcessor(true);
        final MockPostProcessor post2 = new MockPostProcessor(false);
        final MockPostProcessor post3 = new MockPostProcessor(true);

        final AMapper mapper = new AMapperImpl(objectMappingDefs, Collections.emptyList(), AMapperLogger.StdOut, helperFactory, ie, NoContextExtractor.INSTANCE, Collections.emptyList(), Arrays.asList(post1, post2, post3));

        assertNotNull(mapper.map(new DataClass(), type1, AQualifier.NO_QUALIFIER, null, type1, AQualifier.NO_QUALIFIER).get());
        assertEquals(1, post1.count);
        assertEquals(0, post2.count);
        assertEquals(1, post3.count);

        post1.passThrough = false;
        assertNull(mapper.map(new DataClass(), type1, AQualifier.NO_QUALIFIER, null, type1, AQualifier.NO_QUALIFIER).get());
        assertEquals(2, post1.count);
        assertEquals(0, post2.count);
        assertEquals(2, post3.count);

        post3.passThrough = false;
        assertNull(mapper.map(new DataClass(), type1, AQualifier.NO_QUALIFIER, null, type1, AQualifier.NO_QUALIFIER).get());
        assertEquals(3, post1.count);
        assertEquals(0, post2.count);
        assertEquals(3, post3.count);
    }
}

