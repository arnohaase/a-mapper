package com.ajjpj.amapper.javabean.propbased;

import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.diff.ADiffElement;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.path.APathSegment;
import com.ajjpj.amapper.javabean.JavaBeanMappingHelper;

import java.util.Objects;

/**
 * Convenient mapping definition for creating special forward or backward mappings of an attribute.
 * Usable for a common case, where attribute transformation can be implemented without a side effect.
 *
 * @author Roman
 */
public abstract class ASpecialAttributeMapping<S,T,SP,TP> implements APartialBeanMapping<S,T,JavaBeanMappingHelper> {
    final APathSegment sourceChildPath;
    final APathSegment targetChildPath;

    public ASpecialAttributeMapping (APathSegment sourceChildPath, APathSegment targetChildPath) {
        this.sourceChildPath = sourceChildPath;
        this.targetChildPath = targetChildPath;
    }

    public ASpecialAttributeMapping (String sourceName, String targetName) {
        sourceChildPath = APathSegment.simple (sourceName);
        targetChildPath = APathSegment.simple (targetName);
    }

    @Override public String getSourceName () {
        return sourceChildPath.getName ();
    }

    @Override public String getTargetName () {
        return targetChildPath.getName ();
    }

    @Override public void doMap (S source, T target, AMapperWorker<? extends JavaBeanMappingHelper> worker, AMap<String, Object> context, APath path) throws Exception {
        setTargetAttribute (target, mapAttribute (getSourceAttribute (source)));
    }

    @Override public void doDiff (ADiffBuilder diff, S sourceOld, S sourceNew, AMapperDiffWorker<? extends JavaBeanMappingHelper> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
        final TP oldValue = sourceOld==null? null : mapAttribute (getSourceAttribute (sourceOld));
        final TP newValue = sourceNew==null? null : mapAttribute (getSourceAttribute (sourceNew));
        if (!Objects.equals (oldValue, newValue)) {
            diff.add (ADiffElement.attribute (path.withChild (targetChildPath), isDerived, oldValue, newValue));
        }
    }

    /**
     * Gets attribute value from source object, which need to be mapped
     * Handling of null value arguments is expected!
     * @param source source bean
     * @return property to be mapped/diffed
     */
    protected abstract SP getSourceAttribute (S source);

    /**
     * Set or updates transformed source attribute in target bean
     *
     * @param target target bean
     * @param value new target value
     */
    protected abstract void setTargetAttribute (T target, TP value);

    /**
     * Transformation from source attribute value to target attribute value.
     * Needs to be free from side effects!
     * Handling of null value arguments is expected!
     *
     * @param sourceAttribute source property value
     * @return mapped source property
     */
    protected abstract TP mapAttribute (SP sourceAttribute);
}
