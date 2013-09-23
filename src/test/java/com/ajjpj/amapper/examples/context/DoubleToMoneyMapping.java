package com.ajjpj.amapper.examples.context;

import com.ajjpj.amapper.core.*;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import scala.collection.immutable.Map;

/**
 * @author arno
 */
public class DoubleToMoneyMapping implements AValueMappingDef<Double, Money, Object> {
    @Override
    public boolean canHandle(QualifiedSourceAndTargetType types) {
        return types.sourceType().equals(JavaBeanTypes.create(Double.class)) &&
                types.targetType().equals(JavaBeanTypes.create(Money.class));
    }

    @Override
    public Money map(Double sourceValue, QualifiedSourceAndTargetType types, AMapperWorker<?> worker, Map<String, Object> context) {
        if(sourceValue == null) {
            return null;
        }

        final CurrencyProvider curProv = (CurrencyProvider) context.get(CurrencyProvider.class.getName()).get();
        return new Money(sourceValue, curProv.getCurrency());
    }

    @Override
    public void diff(ADiffBuilder diff, Double sourceOld, Double sourceNew, QualifiedSourceAndTargetType types, AMapperWorker<?> worker, Map<String, Object> contextOld, Map<String, Object> contextNew, PathBuilder path, boolean isDerived) {
        throw new UnsupportedOperationException("no diff for this example");
    }
}
