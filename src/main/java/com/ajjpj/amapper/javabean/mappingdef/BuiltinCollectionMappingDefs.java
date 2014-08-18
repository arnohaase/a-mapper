package com.ajjpj.amapper.javabean.mappingdef;

import com.ajjpj.amapper.collection.ACollectionHelper;
import com.ajjpj.amapper.collection.IdentifierBasedCollectionMappingDef;
import com.ajjpj.amapper.core.AObjectMappingDef;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.AType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.SingleParamBeanType;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author arno
 */
public class BuiltinCollectionMappingDefs {
    /**
     * This strategy for mapping sets matches source and target elements by equality of their respective identifiers (as returned by ACollectionHelper
     *  implementations).
     */
    public static final AObjectMappingDef<Object, Object, ACollectionHelper> SetByIdentifierMapping = new IdentifierBasedCollectionMappingDef() {
        @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
            return isBeanCollectionType(types.sourceType) && JavaBeanTypes.isSubtypeOrSameOf (types.targetType, Set.class);
        }
    };

    /**
     * This strategy for mapping lists matches source and target elements by equality of their respective identifiers (as returned by ACollectionHelper
     *  implementations). It assumes that there are no duplicates - if there are, behavior is undefined. This is a conscious trade-off - the overhead
     *  of checking for duplicates is intentionally avoided.
     */
    public static final AObjectMappingDef<Object, Object, ACollectionHelper> ListWithoutDuplicatesByIdentifierMapping = new IdentifierBasedCollectionMappingDef() {
        @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
            return isBeanCollectionType(types.sourceType) && JavaBeanTypes.isSubtypeOrSameOf (types.targetType, List.class);
        }
    };

    //TODO ListAsSetByIdentifierMapping
    //TODO ListByIdentifierMapping

    //TODO SetByEqualityMapping
    //TODO ListByEqualityMapping
    //TODO ListWithoutDuplicatesByEqualityMapping
    //TODO ListAsSetByEqualityMapping

    public static boolean isBeanCollectionType(AType tpe) {
        return tpe instanceof SingleParamBeanType && (
                JavaBeanTypes.isSubtypeOrSameOf (tpe, Collection.class) ||
                JavaBeanTypes.isSubtypeOrSameOf (tpe, Array.class)
        );
    }
}
