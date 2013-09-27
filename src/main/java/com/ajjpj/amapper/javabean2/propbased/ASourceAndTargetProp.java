package com.ajjpj.amapper.javabean2.propbased;

import com.ajjpj.amapper.core2.AMapperDiffWorker;
import com.ajjpj.amapper.core2.AMapperWorker;
import com.ajjpj.amapper.core2.diff.ADiffBuilder;
import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.path.APathSegment;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean2.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean2.propbased.accessors.APropertyAccessor;
import com.ajjpj.amapper.util.coll.AMap;
import com.ajjpj.amapper.util.coll.AOption;
import com.ajjpj.amapper.util.func.AFunction0;
import com.ajjpj.amapper.util.func.AVoidFunction1;

/**
 * @author arno
 */
public class ASourceAndTargetProp<S,T> implements APartialBeanMapping<S,T,JavaBeanMappingHelper> {
    private final APropertyAccessor sourceProp;
    private final APropertyAccessor targetProp;

    private final AQualifiedSourceAndTargetType types;

    public ASourceAndTargetProp(APropertyAccessor sourceProp, APropertyAccessor targetProp) {
        if(! targetProp.isWritable()) {
            throw new IllegalArgumentException("target property is not writable: " + targetProp);
        }

        this.sourceProp = sourceProp;
        this.targetProp = targetProp;

        this.types = new AQualifiedSourceAndTargetType (sourceProp.getType(), sourceProp.getSourceQualifier(), targetProp.getType(), targetProp.getTargetQualifier());
    }

    public ASourceAndTargetProp<T,S> reverse() {
        return new ASourceAndTargetProp<T, S>(targetProp, sourceProp);
    }

    @Override public String getSourceName() {
        return sourceProp.getName();
    }

    @Override public String getTargetName() {
        return targetProp.getName();
    }

    private APath childPath(APath path, boolean isSourceSide) {
        return path.withChild(APathSegment.simple(isSourceSide ? getSourceName() : getTargetName()));
    }

    @Override public void doMap(S source, final T target, AMapperWorker<? extends JavaBeanMappingHelper> worker, AMap<String, Object> context, APath path) throws Exception {
        if(sourceProp.isDeferred()) {
            final AFunction0<Object,Exception> tp = new AFunction0<Object,Exception>() {
                @Override public Object apply() throws Exception {
                    return targetProp.get(target);
                }
            };

            worker.mapDeferred (childPath(path, true), sourceProp.get(source), tp, types, new AVoidFunction1<Object,Exception>() {
                @Override public void apply(Object o) throws Exception {
                    targetProp.set(target, o);
                }
            });
        }
        else {
            final Object oldTargetValue = targetProp.get(target);
            final AOption<Object> opt = worker.map(childPath(path, true), sourceProp.get(source), oldTargetValue, types, context);
            if(opt.isDefined() && opt.get() != oldTargetValue) {
                targetProp.set(target, opt);
            }
        }
    }

    @Override
    public void doDiff(ADiffBuilder diff, S sourceOld, S sourceNew, AMapperDiffWorker<? extends JavaBeanMappingHelper> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
        //TODO use 'default values' (e.g. based on an 'empty' target object) instead of this 'null' default --> add 'getDefaultValue()' to property accessor?
        final Object oldProp = sourceOld != null ? sourceProp.get(sourceOld) : null;
        final Object newProp = sourceNew != null ? sourceProp.get(sourceNew) : null; //TODO does sourceOld/New==null force 'isDerived'?

        if(sourceProp.isDeferred()) {
            worker.diffDeferred(childPath(path, false), oldProp, newProp, types, contextOld, contextNew, isDerived);
        }
        else {
            worker.diff (childPath (path, false), oldProp, newProp, types, contextOld, contextNew, isDerived);
        }
    }
}

