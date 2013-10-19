package com.ajjpj.amapper.core.compile;


import com.ajjpj.amapper.core.AValueMappingDef;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.util.coll.AOption;

/**
 * @author arno
 */
public interface ACompilationContext {
    AOption<AValueMappingDef> tryGetValueMapping(AQualifiedSourceAndTargetType types) throws Exception;
}
