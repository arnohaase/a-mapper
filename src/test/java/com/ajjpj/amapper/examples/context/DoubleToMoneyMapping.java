package com.ajjpj.amapper.examples.context;

import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.AValueMappingDef;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;


/**
 * @author arno
 */
public class DoubleToMoneyMapping implements AValueMappingDef<Double, Money, Object> {
    @Override
    public boolean canHandle(AQualifiedSourceAndTargetType types) {
        return types.sourceType.equals(JavaBeanTypes.create(Double.class)) &&
               types.targetType.equals(JavaBeanTypes.create(Money.class));
    }

    @Override
    public Money map(Double sourceValue, AQualifiedSourceAndTargetType types, AMapperWorker<?> worker, AMap<String, Object> context) {
        if(sourceValue == null) {
            return null;
        }

        final CurrencyProvider curProv = (CurrencyProvider) context.get(CurrencyProvider.class.getName()).get();
        return new Money(sourceValue, curProv.getCurrency());
    }

    @Override
    public void diff(ADiffBuilder diff, Double sourceOld, Double sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
        throw new UnsupportedOperationException("no diff for this example");
    }
}
