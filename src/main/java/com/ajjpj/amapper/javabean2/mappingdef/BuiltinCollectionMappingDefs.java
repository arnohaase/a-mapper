package com.ajjpj.amapper.javabean2.mappingdef;

import com.ajjpj.amapper.collection2.ACollectionHelper;
import com.ajjpj.amapper.collection2.IdentifierBasedCollectionMappingDef;
import com.ajjpj.amapper.core2.AObjectMappingDef;
import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core2.tpe.AType;
import com.ajjpj.amapper.javabean2.JavaBeanTypes;
import com.ajjpj.amapper.javabean2.SingleParamBeanType;

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
