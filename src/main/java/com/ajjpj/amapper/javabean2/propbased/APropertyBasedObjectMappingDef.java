package com.ajjpj.amapper.javabean2.propbased;

import com.ajjpj.amapper.core2.AMapperDiffWorker;
import com.ajjpj.amapper.core2.AMapperWorker;
import com.ajjpj.amapper.core2.diff.ADiffBuilder;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean2.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean2.mappingdef.AbstractJavaBeanObjectMappingDef;
import com.ajjpj.amapper.util.coll.AMap;

import java.util.Collection;

/**
 * @author arno
 */
public class APropertyBasedObjectMappingDef<S,T,H extends JavaBeanMappingHelper> extends AbstractJavaBeanObjectMappingDef<S,T,H> {
    private final Collection<? extends APartialBeanMapping<S,T,? super H>> parts;

    public APropertyBasedObjectMappingDef(Class<S> sourceClass, Class<T> targetClass, Collection<? extends APartialBeanMapping<S, T, ? super H>> parts) {
        super(sourceClass, targetClass);
        this.parts = parts;
        System.out.println(sourceClass.getName() + "-> " + targetClass.getName() + ": " + parts);
    }

    @Override protected void doMap(S source, T target, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context, APath path) throws Exception {
        for(APartialBeanMapping<S,T,? super H> part: parts) {
            part.doMap(source, target, worker, context, path);
        }
    }

    @Override public void diff(ADiffBuilder diff, S sourceOld, S sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
        for(APartialBeanMapping<S,T,? super H> part: parts) {
            part.doDiff(diff, sourceOld, sourceNew, worker, contextOld, contextNew, path, isDerived);
        }
    }
}
