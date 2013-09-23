package com.ajjpj.amapper.examples.valuemappingdef;


import com.ajjpj.amapper.core.*;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import scala.collection.immutable.Map;

import java.util.Date;

/**
 * @author arno
 */
public class DateToLongValueMappingDef implements AValueMappingDef<Date, Long, Object> {
    @Override
    public boolean canHandle(QualifiedSourceAndTargetType types) {
        return
                types.sourceType().equals (JavaBeanTypes.create(Date.class)) &&
                types.targetType().equals (JavaBeanTypes.create(Long.class));
    }

    @Override
    public Long map(Date sourceValue, QualifiedSourceAndTargetType types, AMapperWorker<?> worker, Map<String, Object> context) {
        return sourceValue == null ? null : sourceValue.getTime();
    }

    @Override
    public void diff(ADiffBuilder diff, Date sourceOld, Date sourceNew, QualifiedSourceAndTargetType types, AMapperWorker<?> worker, Map<String, Object> contextOld, Map<String, Object> contextNew, PathBuilder path, boolean isDerived) {
        if(nullSafeEquals(sourceOld, sourceNew))
            return;

        final Long oldTime = sourceOld == null ? null : sourceOld.getTime();
        final Long newTime = sourceNew == null ? null : sourceNew.getTime();

        diff.add(new AttributeDiffElement(path.build(), oldTime, newTime, isDerived));
    }

    private boolean nullSafeEquals(Date d1, Date d2) {
        if (d1 == null)
            return d2 == null;
        else
            return d1.equals(d2);
    }
}
