package com.ajjpj.amapper.javabean.mappingdef;

import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.amapper.collection.ACollectionHelper;
import com.ajjpj.amapper.core.AIdentifierExtractor;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.AObjectMappingDef;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.AQualifiedType;
import com.ajjpj.amapper.core.tpe.AType;
import com.ajjpj.amapper.javabean.JavaBeanType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.SingleParamBeanType;
import com.ajjpj.amapper.util.AArraySupport;

import java.lang.reflect.Array;
import java.util.*;


/**
 * @author arno
 */
class CollectionToArrayMappingDef implements AObjectMappingDef<Object, Object, ACollectionHelper> {
    @Override public boolean canHandle (AQualifiedSourceAndTargetType types) throws Exception {
        return
                BuiltinCollectionMappingDefs.isBeanCollectionType (types.sourceType()) &&
                types.targetType() instanceof JavaBeanType &&
                ((JavaBeanType) types.targetType()).cls.isArray ();
    }

    @Override public boolean isCacheable () {
        return true;
    }

    @SuppressWarnings ("unchecked")
    @Override public Object map (Object source, Object target, AQualifiedSourceAndTargetType types, AMapperWorker<? extends ACollectionHelper> worker, AMap<String, Object> context, APath path) throws Exception {
        if (source == null) {
            return null;
        }

        final AQualifiedType sourceElementType = worker.getHelpers ().elementType (types.source ());
        final AQualifiedType targetElementType = worker.getHelpers ().elementType (types.target ());

        final AQualifiedSourceAndTargetType elTypes = AQualifiedSourceAndTargetType.create (sourceElementType, targetElementType);

        final Map<Object, Object> targetValuesByIdentifier = byIdentifier (worker.getHelpers ().asJuCollection (target, types.target ()),
                worker.getIdentifierExtractor (),
                targetElementType, // this is intentional: extract identifiers for target elements in terms of the target element type
                targetElementType);


        final List<Object> mappedTargetList = new ArrayList<> ();
        for (Object sourceEl: worker.getHelpers ().asJuCollection (source, types.source ())) {
            final Object sourceIdent = worker.getIdentifierExtractor ().uniqueIdentifier (sourceEl, sourceElementType, targetElementType);

            final APath elPath = path.withElementChild (sourceIdent);
            final AOption<Object> mappedOpt = worker.map (elPath, sourceEl, targetValuesByIdentifier.get (sourceIdent), elTypes, context);
            if (mappedOpt.isDefined ()) {
                mappedTargetList.add (mappedOpt.get ());
            }
        }

        final Class<?> targetElementClass = ((SingleParamBeanType<?,?>) types.targetType()).paramCls;
        if (target == null || Array.getLength (target) != mappedTargetList.size ()) {
            target = Array.newInstance (targetElementClass, mappedTargetList.size ());
        }

        AArraySupport.setValues (target, mappedTargetList);

        return target;
    }

    private Map<Object, Object> byIdentifier (Collection<?> coll, AIdentifierExtractor identifierExtractor, AQualifiedType type, AQualifiedType targetType) {
        final Map<Object, Object> result = new HashMap<> ();

        if (coll == null) {
            return result;
        }

        for (Object o: coll) {
            result.put (identifierExtractor.uniqueIdentifier (o, type, targetType), o);
        }

        return result;
    }

    //TODO test this
    @Override public void diff (ADiffBuilder diff, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends ACollectionHelper> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
        final AType targetElementType = worker.getHelpers ().elementType (types.target ()).tpe;
        final AType targetTypeAsList = JavaBeanTypes.create (List.class, ((JavaBeanType) targetElementType).cls);
        final AQualifiedSourceAndTargetType typesWithListTarget = AQualifiedSourceAndTargetType.create (types.source (), new AQualifiedType (targetTypeAsList, types.targetQualifier ()));

        worker.diff (path, sourceOld, sourceNew, typesWithListTarget, contextOld, contextNew, isDerived);
    }
}
