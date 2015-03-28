package com.ajjpj.amapper.core.compile;


import com.ajjpj.afoundation.collection.immutable.AOption;
import com.ajjpj.amapper.core.AValueMappingDef;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;

/**
 * @author arno
 */
public interface ACompilationContext {
    AOption<AValueMappingDef> tryGetValueMapping(AQualifiedSourceAndTargetType types) throws Exception;
}
