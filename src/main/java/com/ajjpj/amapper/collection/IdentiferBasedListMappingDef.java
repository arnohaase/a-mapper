package com.ajjpj.amapper.collection;

import com.ajjpj.abase.collection.AEquality;
import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.abase.function.AFunction2NoThrow;
import com.ajjpj.amapper.core.AIdentifierExtractor;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.AObjectMappingDef;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *  This class handles list objects based on the unique identifiers of their elements. A source and a target element correspond
 *  if their identifiers are equal.
 *
 *  Order of elements in a list is a factor, this implementation considers. So List(A,B) is not equal to List(B,A)
 *  Transformation of target list is done with an algorithm based on Levenshtein distance
 *
 * @author Roman
 */
public class IdentiferBasedListMappingDef implements AObjectMappingDef<Object, Object, ACollectionHelper> {

    @Override public boolean isCacheable () {
        return true;
    }

    @Override public Object map (Object source, Object target,
                                 final AQualifiedSourceAndTargetType types,
                                 final AMapperWorker<? extends ACollectionHelper> worker,
                                 final AMap<String, Object> context,
                                 final APath path) throws Exception {
        if (source == null) {
            return null;
        }

        final ACollectionHelper h = worker.getHelpers();
        final Collection<Object> sourceColl = h.asJuCollection (source, types.source());
        final List<Object> targetColl = (List) h.asJuCollection (target, types.target());
        final AQualifiedSourceAndTargetType elementTypes = AQualifiedSourceAndTargetType.create (h.elementType (types.source()), h.elementType(types.target()));

        if (targetColl.isEmpty()) {
            // this is an optimization for the common case that the target collection is initially empty
            for (Object s: sourceColl) {
                final APath elPath = path.withElementChild (worker.getIdentifierExtractor().uniqueIdentifier (s, types.source (), types.target ()));

                final AOption<Object> optT = worker.map (elPath, s, null, elementTypes, context);
                if (optT.isDefined()) {
                    targetColl.add(optT.get());
                }
            }
            return h.fromJuCollection(targetColl, types.target());
        }

        final AIdentifierExtractor identifierExtractor = worker.getIdentifierExtractor();

        final AFunction2NoThrow <Object, Object, Boolean> eqFunction = new AFunction2NoThrow<Object, Object, Boolean> () {
            @Override
            public Boolean apply (Object param1, Object param2) {
                final Object sourceIdent = identifierExtractor.uniqueIdentifier (param1, types.source (), types.target ());
                final AOption<Object> equivTarget = findTarget (targetColl, sourceIdent, identifierExtractor, types);
                return equivTarget.isDefined ();
            }
        };

        final AFunction2NoThrow <Object, Object, AOption<Object>> mapFunction = new AFunction2NoThrow<Object, Object, AOption<Object>> () {
            @Override public AOption<Object> apply (Object s, Object t) {
                final APath elPath = path.withElementChild (worker.getIdentifierExtractor().uniqueIdentifier (s, types.source (), types.target ()));
                return worker.map (elPath, s, t, elementTypes, context);
            }
        };

        LevenshteinDistance<Object, Object> levenshteinDistance = new LevenshteinDistance<> (sourceColl, targetColl, eqFunction, mapFunction);
        levenshteinDistance.editTarget();

        return h.fromJuCollection(targetColl, types.target());
    }

    private static AOption<Object> findTarget(List<Object> targetColl, Object ident, AIdentifierExtractor identifierExtractor, AQualifiedSourceAndTargetType types) {
        for (Object candidate: targetColl) {
            if (AEquality.EQUALS.equals (ident, identifierExtractor.uniqueIdentifier (candidate, types.target (), types.target ()))) {
                return AOption.some(candidate);
            }
        }
        return AOption.none();
    }


    @Override public void diff (ADiffBuilder diff,
                                Object sourceOld, Object sourceNew,
                                AQualifiedSourceAndTargetType types,
                                AMapperDiffWorker<? extends ACollectionHelper> worker,
                                AMap<String, Object> contextOld,
                                AMap<String, Object> contextNew,
                                APath path,
                                boolean isDerived) throws Exception {

    }

    @Override public boolean canHandle (AQualifiedSourceAndTargetType types) throws Exception {
        return BuiltinCollectionMappingDefs.isBeanCollectionType (types.sourceType ()) && JavaBeanTypes.isSubtypeOrSameOf (types.targetType (), List.class);
    }
}
