package com.ajjpj.amapper.javabean2.mappingdef;

import com.ajjpj.amapper.core2.AMapperWorker;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean2.JavaBeanMappingHelper;
import com.ajjpj.amapper.util.coll.AMap;

/**
 * This superclass provides the typical type handling for Java Bean mapping defs. It provides a convenience
 *  canHandle() implementation, and it creates new instances when the target ref is <code>null</code>. <p />
 *
 * If you are writing an object mapping def for Java Beans and want typical, 'default' behavior, extend this
 *  class.<p />
 *
 * This class' canHandle() implementation checks for type equality, ignoring qualifiers. If you want qualifier
 *  dependence, one of the static methods in MappingDefTools may be a convenient way to go.
 *
 * @author arno
 */
public abstract class AbstractJavaBeanObjectMappingDef<S,T,H extends JavaBeanMappingHelper> extends AbstractJavaBeanObjectMappingDefWithoutDefaultNullHandling<S,T,H> {
    public AbstractJavaBeanObjectMappingDef(Class<S> sourceClass, Class<T> targetClass) {
        super(sourceClass, targetClass);
    }

    @Override public boolean isCacheable() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override public final T map(S source, T targetRaw, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context, APath path) throws Exception {
        if(source == null) {
            return null;
        }
        else {
            final T target = targetRaw != null ? targetRaw : (T) worker.getHelpers().createInstance(sourceType, targetType);
            doMap(source, target, types, worker, context, path);
            return target;
        }
    }

    protected abstract void doMap(S source, T target, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context, APath path) throws Exception;
}
