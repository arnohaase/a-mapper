package com.ajjpj.amapper.examples.doubleToBigDecimalQualifier;

import com.ajjpj.amapper.core2.AMapperDiffWorker;
import com.ajjpj.amapper.core2.AMapperWorker;
import com.ajjpj.amapper.core2.AValueMappingDef;
import com.ajjpj.amapper.core2.diff.ADiffBuilder;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean2.JavaBeanTypes;
import com.ajjpj.amapper.util.coll.AMap;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author arno
 */
public class DoubleToBigDecimalForCurrencyMappingDef implements AValueMappingDef<Double, BigDecimal, Object> {
    @Override
    public boolean canHandle(AQualifiedSourceAndTargetType types) {
        return types.sourceType.equals(JavaBeanTypes.create(Double.class)) &&
               types.targetType.equals(JavaBeanTypes.create(BigDecimal.class)) &&
               types.targetQualifier.get("Currency Rounding").isDefined();
    }

    @Override
    public BigDecimal map(Double sourceValue, AQualifiedSourceAndTargetType types, AMapperWorker<?> worker, AMap<String, Object> context) {
        if(sourceValue == null) {
            return null;
        }

        return BigDecimal.valueOf(sourceValue).setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public void diff(ADiffBuilder diff, Double sourceOld, Double sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
        throw new UnsupportedOperationException("unimplemented for this example");
    }
}
