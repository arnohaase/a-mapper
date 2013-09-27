package com.ajjpj.amapper.javabean2.mappingdef;


import com.ajjpj.amapper.core2.AMapperDiffWorker;
import com.ajjpj.amapper.core2.AMapperWorker;
import com.ajjpj.amapper.core2.AValueMappingDef;
import com.ajjpj.amapper.core2.diff.ADiffBuilder;
import com.ajjpj.amapper.core2.diff.ADiffElement;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean2.JavaBeanType;
import com.ajjpj.amapper.javabean2.JavaBeanTypes;
import com.ajjpj.amapper.util.coll.AEquality;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * @author arno
 */
abstract class FromNumberValueMappingDef<T> implements AValueMappingDef<Number, T, Object> {
    protected final JavaBeanType<T> tpe;

    protected FromNumberValueMappingDef(Class<T> cls) {
        this.tpe = JavaBeanTypes.create(cls);
    }

    @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
        if(! types.targetType.equals(tpe)) {
            return false;
        }

        return types.sourceType instanceof JavaBeanType && JavaBeanTypes.isSubtypeOrSameOf(types.sourceType, Number.class);
    }

    @Override public T map(Number sourceValue, AQualifiedSourceAndTargetType types, AMapperWorker<?> worker, AMap<String, Object> context) {
        return sourceValue != null ? fromNumber(sourceValue) : null;
    }

    @Override public void diff(ADiffBuilder diff, Number sourceOld, Number sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
        final T exOld = sourceOld != null ? fromNumber(sourceOld) : null;
        final T exNew = sourceNew != null ? fromNumber(sourceNew) : null;

        if(!AEquality.EQUALS.equals(exOld, exNew)) {
            diff.add(ADiffElement.attribute(path, isDerived, exOld, exNew));
        }
    }

    protected abstract T fromNumber(Number n);
}
