package com.ajjpj.amapper.core;

import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.CanHandleSourceAndTarget;
import com.ajjpj.amapper.util.coll.AOption;

/**
 * @author arno
 */
public interface APreProcessor extends CanHandleSourceAndTarget {
    /**
     * transforms a source object before it is actually mapped. Returning Some(x) causes 'x' to be used for the
     *  actual processing (which may or may not be the same object as 'o'), while returning None causes the
     *  object to be ignored for actual processing.
     */
    <T> AOption<T> preProcess(T o, AQualifiedSourceAndTargetType qt);

    APreProcessor NO_PREPROCESSOR = new NoPreprocessor();

    class NoPreprocessor implements APreProcessor {
        @Override public boolean canHandle(AQualifiedSourceAndTargetType sourceAndTarget) {
            return true;
        }

        @Override public <T> AOption<T> preProcess(T o, AQualifiedSourceAndTargetType qt) {
            return AOption.some(o);
        }
    }
}
