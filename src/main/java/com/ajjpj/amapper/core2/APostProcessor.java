package com.ajjpj.amapper.core2;

import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core2.tpe.CanHandleSourceAndTarget;


/**
 * @author arno
 */
public interface APostProcessor extends CanHandleSourceAndTarget {
    <T> T postProcess(T o, AQualifiedSourceAndTargetType qt);

    APostProcessor NO_POSTPROCESSOR = new NoPostProcessor();

    class NoPostProcessor implements APostProcessor {
        @Override public boolean canHandle(AQualifiedSourceAndTargetType sourceAndTarget) {
            return true;
        }

        @Override public <T> T postProcess(T o, AQualifiedSourceAndTargetType qt) {
            return o;
        }
    }
}
