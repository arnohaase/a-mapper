package com.ajjpj.amapper.collection;

import com.ajjpj.abase.collection.AEquality;
import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.abase.function.AFunction2NoThrow;
import com.ajjpj.abase.function.APredicate2NoThrow;
import com.ajjpj.abase.util.AObjectHolder;
import com.ajjpj.amapper.core.AIdentifierExtractor;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.AObjectMappingDef;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.mappingdef.BuiltinCollectionMappingDefs;

import java.util.*;

/**
 *  This class handles list objects based on the unique identifiers of their elements. A source and a target element correspond
 *  if their identifiers are equal.
 *
 *  Order of elements in a list is a factor, this implementation considers. So List(A,B) is not equal to List(B,A)
 *  Transformation of target list is done with an algorithm based on Levenshtein distance
 *
 * @author Roman
 */
public class LevenshteinBasedListMappingDef implements AObjectMappingDef<Object, Object, ACollectionHelper> {

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
        final List<Object> targetColl = (List<Object>) h.asJuCollection (target, types.target());
        final AQualifiedSourceAndTargetType elementTypes = AQualifiedSourceAndTargetType.create (h.elementType (types.source()), h.elementType(types.target()));
        final AIdentifierExtractor identifierExtractor = worker.getIdentifierExtractor();

//        if (targetColl.isEmpty()) {
//            int index=0;
//            // this is an optimization for the common case that the target collection is initially empty
//            for (Object s: sourceColl) {
//                final APath elPath = path.withElementChild (index++, identifierExtractor.uniqueIdentifier (s, types.source (), types.target ()));
//                final AOption<Object> optT = worker.map (elPath, s, null, elementTypes, context);
//                if (optT.isDefined()) {
//                    targetColl.add(optT.get());
//                }
//            }
//            return h.fromJuCollection(targetColl, types.target());
//        }

        final APredicate2NoThrow<Object, Object> eqPredicate = new APredicate2NoThrow<Object, Object> () {
            @Override public boolean apply (Object param1, Object param2) {
                final Object sourceIdent = identifierExtractor.uniqueIdentifier (param1, types.source (), types.target ());
                final Object targetIdent = identifierExtractor.uniqueIdentifier (param2, types.target (), types.target ());
                return Objects.equals (sourceIdent, targetIdent);
            }
        };

        final AObjectHolder<Integer> index = new AObjectHolder<> (0);
        final AFunction2NoThrow <Object, Object, AOption<Object>> mapFunction = new AFunction2NoThrow<Object, Object, AOption<Object>> () {
            @Override public AOption<Object> apply (Object s, Object t) {
                final APath elPath = path.withElementChild (index.value++, identifierExtractor.uniqueIdentifier (s, types.source (), types.target ()));
                return worker.map (elPath, s, t, elementTypes, context);
            }
        };

        LevenshteinDistance<Object, Object> levenshteinDistance = new LevenshteinDistance<> (sourceColl, targetColl, eqPredicate);
        levenshteinDistance.editTarget (mapFunction);

        return h.fromJuCollection(targetColl, types.target());
    }

    @Override public void diff (ADiffBuilder diff,
                                final Object sourceOld, final Object sourceNew,
                                final AQualifiedSourceAndTargetType types,
                                AMapperDiffWorker<? extends ACollectionHelper> worker,
                                AMap<String, Object> contextOld,
                                AMap<String, Object> contextNew,
                                APath path,
                                boolean isDerived) throws Exception {
        final ACollectionHelper h = worker.getHelpers();

        final List<Object> oldColl = (List<Object>) h.asJuCollection(sourceOld, types.source());
        final List<Object> newColl = (List<Object>) h.asJuCollection(sourceNew, types.source());

        final AQualifiedSourceAndTargetType elementTypes = AQualifiedSourceAndTargetType.create (h.elementType(types.source()), h.elementType(types.target()));

        final AIdentifierExtractor identifierExtractor = worker.getIdentifierExtractor();

        final APredicate2NoThrow <Object, Object> equalsPredicate = new APredicate2NoThrow<Object, Object> () {
            @Override public boolean apply (Object param1, Object param2) {
                final Object ident1 = identifierExtractor.uniqueIdentifier (param1, types.source(), types.target());
                final Object ident2 = identifierExtractor.uniqueIdentifier (param2, types.source(), types.target());
                return Objects.equals (ident1, ident2);
            }
        };

        LevenshteinDistance<Object, Object> levenshtein = new LevenshteinDistance<> (newColl, oldColl, equalsPredicate);
        List<LevenshteinDistance.EditChoice> editPath = levenshtein.getEditPath();

        int i=0;
        int j=0;
        for (LevenshteinDistance.EditChoice c: editPath) {
            final APath elPath = path.withElementChild (j, worker.getIdentifierExtractor().uniqueIdentifier (oldColl.get (j), elementTypes.source (), elementTypes.target ()));
            switch (c) {
                case replace:
                case noOp: {
                    worker.diff (elPath, oldColl.get (j), newColl.get (i), elementTypes, contextOld, contextNew, isDerived);
                    i++;
                    j++;
                    break;
                }
                case delete: {
                    worker.diff (elPath, oldColl.get (j), null, elementTypes, contextOld, contextNew, true);
                    j++;
                    break;
                }
                case insert: {
                    worker.diff (elPath, null, newColl.get (i), elementTypes, contextOld, contextNew, true);
                    i++;
                    break;
                }
            }

        }
    }

    @Override public boolean canHandle (AQualifiedSourceAndTargetType types) throws Exception {
        return BuiltinCollectionMappingDefs.isBeanCollectionType (types.sourceType ()) && JavaBeanTypes.isSubtypeOrSameOf (types.targetType (), List.class);
    }
}
