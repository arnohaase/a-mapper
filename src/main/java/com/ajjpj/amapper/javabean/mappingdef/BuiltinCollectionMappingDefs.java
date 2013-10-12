package com.ajjpj.amapper.javabean.mappingdef;

import com.ajjpj.amapper.collection.ACollectionHelper;
import com.ajjpj.amapper.collection.IdentifierBasedCollectionMappingDef;
import com.ajjpj.amapper.core.AObjectMappingDef;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.AType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.SingleParamBeanType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author arno
 */
public class BuiltinCollectionMappingDefs {
    public static final AObjectMappingDef<Object, Object, ACollectionHelper> SetByIdentifierMapping = new IdentifierBasedCollectionMappingDef() {
        @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
            return isBeanCollectionType(types.sourceType) && JavaBeanTypes.isSubtypeOrSameOf (types.targetType, Set.class);
        }
    };

    /**
     * This strategy for mapping lists matches source and target elements by equality of their respective identifiers (as returned by ACollectionHelper
     *  implementations). It assumes that there are no duplicates - if there are, behavior is undefined. This is a conscious trade-off - the overhead
     *  of checking for duplicates is intentionally avoided. TODO ListAsSetByIdentifier
     */
    public static final AObjectMappingDef<Object, Object, ACollectionHelper> ListByIdentifierMapping = new IdentifierBasedCollectionMappingDef() {
        @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
            return isBeanCollectionType(types.sourceType) && JavaBeanTypes.isSubtypeOrSameOf (types.targetType, List.class);
        }
    };

    public static boolean isBeanCollectionType(AType tpe) {
        return tpe instanceof SingleParamBeanType && JavaBeanTypes.isSubtypeOrSameOf(tpe, Collection.class);
    }
}
