package com.ajjpj.amapper.core;

import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.CanHandleSourceAndTarget;


/**
 * @author arno
 */
public interface APostProcessor extends CanHandleSourceAndTarget {
    <T> T postProcess(T o, AQualifiedSourceAndTargetType qt);   //TODO pass in ref to source element!

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
