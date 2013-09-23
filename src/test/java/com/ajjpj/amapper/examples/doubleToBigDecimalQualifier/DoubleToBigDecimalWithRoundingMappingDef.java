package com.ajjpj.amapper.examples.doubleToBigDecimalQualifier;

import com.ajjpj.amapper.core.*;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import scala.collection.immutable.Map;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author arno
 */
public class DoubleToBigDecimalWithRoundingMappingDef implements AValueMappingDef<Double, BigDecimal, Object> {
    @Override
    public boolean canHandle(QualifiedSourceAndTargetType types) {
        return types.sourceType().equals(JavaBeanTypes.create(Double.class)) &&
                types.targetType().equals(JavaBeanTypes.create(BigDecimal.class)) &&
                types.targetQualifier().get("round n digits").isDefined();
    }

    @Override
    public BigDecimal map(Double sourceValue, QualifiedSourceAndTargetType types, AMapperWorker<?> worker, Map<String, Object> context) {
        if(sourceValue == null) {
            return null;
        }

        final int numDigits = Integer.valueOf(types.targetQualifier().get("round n digits").get());
        return BigDecimal.valueOf(sourceValue).setScale(numDigits, RoundingMode.HALF_UP);
    }

    @Override
    public void diff(ADiffBuilder diff, Double sourceOld, Double sourceNew, QualifiedSourceAndTargetType types, AMapperWorker<?> worker, Map<String, Object> contextOld, Map<String, Object> contextNew, PathBuilder path, boolean isDerived) {
        throw new UnsupportedOperationException("unimplemented for this example");
    }
}
