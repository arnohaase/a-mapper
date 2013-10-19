package com.ajjpj.amapper.javabean.mappingdef;

import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.AValueMappingDef;
import com.ajjpj.amapper.core.compile.ACodeSnippet;
import com.ajjpj.amapper.core.compile.AInlineableValueMappingDef;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.diff.ADiffElement;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.JavaBeanType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.util.coll.AEquality;
import com.ajjpj.amapper.util.coll.AMap;


/**
 * @author arno
 */
public class PassThroughValueMappingDef<T> implements AValueMappingDef<T,T,Object>, AInlineableValueMappingDef { //TODO show this in the documentation
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

    @Override public ACodeSnippet javaCodeForMap(ACodeSnippet sourceValue, AQualifiedSourceAndTargetType types) {
        return new ACodeSnippet(sourceValue.getCode());
    }

    @Override public ACodeSnippet javaCodeForDiff(ACodeSnippet sourceOld, ACodeSnippet sourceNew, AQualifiedSourceAndTargetType types) {
        final String result = "if(! com.ajjpj.amapper.util.coll.AEquality.EQUALS.equals(" + sourceOld.getCode() + ", " + sourceNew.getCode() +
                ") { diff.add(com.ajjpj.amapper.core.diff.ADiffElement.attribute(path, isDerived, " + sourceOld.getCode() + ", " + sourceNew.getCode() + "; }";

        return new ACodeSnippet(result);
    }
}
