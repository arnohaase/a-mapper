package com.ajjpj.amapper.core.compile;

import com.ajjpj.amapper.core.AValueMappingDef;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.CanHandleSourceAndTargetCache;
import com.ajjpj.amapper.util.coll.AOption;

import java.util.Collection;

/**
 * @author arno
 */
class ACompilationContextImpl implements ACompilationContext {
    private final CanHandleSourceAndTargetCache<AValueMappingDef, AValueMappingDef> valueMappingCache;

    ACompilationContextImpl(Collection<? extends AValueMappingDef> valueMappings) {
        this.valueMappingCache = new CanHandleSourceAndTargetCache<AValueMappingDef, AValueMappingDef>("no value mapping def found for ", valueMappings);
    }

    @Override public AOption<AValueMappingDef> tryGetValueMapping(AQualifiedSourceAndTargetType types) throws Exception {
        return valueMappingCache.tryEntryFor(types);
    }
}
