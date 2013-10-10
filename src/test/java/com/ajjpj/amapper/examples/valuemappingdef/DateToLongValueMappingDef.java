package com.ajjpj.amapper.examples.valuemappingdef;

import com.ajjpj.amapper.core2.AMapperDiffWorker;
import com.ajjpj.amapper.core2.AMapperWorker;
import com.ajjpj.amapper.core2.AValueMappingDef;
import com.ajjpj.amapper.core2.diff.ADiffBuilder;
import com.ajjpj.amapper.core2.diff.ADiffElement;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean2.JavaBeanTypes;
import com.ajjpj.amapper.util.coll.AMap;

import java.util.Date;


/**
 * @author arno
 */
public class DateToLongValueMappingDef implements AValueMappingDef<Date, Long, Object> {
    @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
        return
                types.sourceType.equals (JavaBeanTypes.create(Date.class)) &&
                types.targetType.equals (JavaBeanTypes.create(Long.class));
    }

    @Override
    public Long map(Date sourceValue, AQualifiedSourceAndTargetType types, AMapperWorker<?> worker, AMap<String, Object> context) {
        return sourceValue == null ? null : sourceValue.getTime();
    }

    @Override
    public void diff(ADiffBuilder diff, Date sourceOld, Date sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
        if(nullSafeEquals(sourceOld, sourceNew))
            return;

        final Long oldTime = sourceOld == null ? null : sourceOld.getTime();
        final Long newTime = sourceNew == null ? null : sourceNew.getTime();

        diff.add(ADiffElement.attribute(path, isDerived, oldTime, newTime));
    }

    private boolean nullSafeEquals(Date d1, Date d2) {
        if (d1 == null)
            return d2 == null;
        else
            return d1.equals(d2);
    }
}
