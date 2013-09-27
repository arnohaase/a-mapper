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
class PassThroughValueMappingDef<T> implements AValueMappingDef<T,T,Object> {
    protected final JavaBeanType<T> tpe;

    PassThroughValueMappingDef(Class<T> cls) {
        this.tpe = JavaBeanTypes.create(cls);
    }

    @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
        return tpe.equals(types.sourceType) && tpe.equals (types.targetType);
    }

    @Override public T map(T sourceValue, AQualifiedSourceAndTargetType types, AMapperWorker<?> worker, AMap<String, Object> context) {
        return sourceValue;
    }

    @Override public void diff(ADiffBuilder diff, T sourceOld, T sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<?> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) {
        if(! AEquality.EQUALS.equals(sourceOld, sourceNew)) {
            diff.add(ADiffElement.attribute(path, isDerived, sourceOld, sourceNew));
        }
    }
}
