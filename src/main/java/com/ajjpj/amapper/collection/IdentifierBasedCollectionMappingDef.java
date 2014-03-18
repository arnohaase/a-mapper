package com.ajjpj.amapper.collection;

import com.ajjpj.abase.collection.AEquality;
import com.ajjpj.abase.collection.immutable.AMap;
import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.amapper.core.AIdentifierExtractor;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.AObjectMappingDef;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;

import java.util.*;

/**
 * This class handles collections based on the unique identifiers of their elements. A source and a target element correspond
 *  if their identifiers are equal.<p />
 *
 * That means that all collections are mapped using 'set' semantics, regardless of their actual type and 'native' semantics. For
 *  java.util.Lists, that can e.g. be useful if they are mapped with Hibernate as 'bags'.<p />
 *
 * More specifically, the class makes the following assumptions:
 *
 * <ul>
 *     <li> neither source nor target collection have two elements with the same identifier
 *     <li> element order / iteration order is considered insignificant
 * </ul>
 *
 * If these assumptions are not met, unexpected behavior may result.<p />
 *
 * As it stands, this class 'can handle' no types at all. That is intentional - in order to actually use it, wrap it using <
 *  code>MappingDefTools.forTypes()</code>. <p />
 *
 * Qualifiers of collection types are used for their elements as well.<p />
 *
 * Both map() and diff() operations have O(n^2) performance.
 *
 * @author arno
 */
public class IdentifierBasedCollectionMappingDef implements AObjectMappingDef<Object, Object, ACollectionHelper> {
    /**
     * handles nothing by default - specialize this using TODO in order to use it
     */
    @Override public boolean canHandle(AQualifiedSourceAndTargetType types) {
        return false;
    }

    @Override public boolean isCacheable() {
        return true;
    }

    @Override public Object map(Object source, Object target, AQualifiedSourceAndTargetType types, AMapperWorker<? extends ACollectionHelper> worker, AMap<String, Object> context, APath path) throws Exception {
        final ACollectionHelper h = worker.getHelpers();

        final Collection<Object> sourceColl = h.asJuCollection(source, types.source());
        if(source == null) {
            return null;
        }

        final Collection<Object> targetColl = target != null ? h.asJuCollection(target, types.target()) : h.createEmptyCollection(types.target());
        final AQualifiedSourceAndTargetType elementTypes = new AQualifiedSourceAndTargetType(h.elementType(types.sourceType), types.sourceQualifier, h.elementType(types.targetType), types.targetQualifier);

        if(targetColl.isEmpty()) {
            // this is an optimization for the common case that the target collection is initially empty
            for(Object s: sourceColl) {
                final APath elPath = ACollectionMappingTools.elementPath(path, worker.getIdentifierExtractor().uniqueIdentifier(s, types));

                final AOption<Object> optT = worker.map(elPath, s, null, elementTypes, context);
                if(optT.isDefined()) {
                    targetColl.add(optT.get());
                }
            }
            return h.fromJuCollection(targetColl, types.target());
        }

        final Equiv equiv = new Equiv(sourceColl, targetColl, types, worker.getIdentifierExtractor());

        // now apply the changes to the target collection
        targetColl.removeAll(equiv.targetWithoutSource);
        for (Object s: equiv.sourceWithoutTarget) {
            final APath elPath = ACollectionMappingTools.elementPath(path, worker.getIdentifierExtractor().uniqueIdentifier(s, types));
            final AOption<Object> tc = worker.map(elPath, s, null, elementTypes, context);
            if(tc.isDefined()) {
                targetColl.add(tc.get());
            }
        }
        for (Map.Entry<Object, Object> e: equiv.equiv.entrySet()) {
            final APath elPath = ACollectionMappingTools.elementPath(path, worker.getIdentifierExtractor().uniqueIdentifier(e.getKey(), types));
            final AOption<Object> tc = worker.map(elPath, e.getKey(), e.getValue(), elementTypes, context);

            if(tc.isEmpty()) {
                targetColl.remove(e.getValue());
            }
            else if(tc.get() != e.getValue()) {
                targetColl.remove(e.getValue());
                targetColl.add(tc.get());
            }
            // tc.get() == e.getValue(): element mapping just modified the existing element
        }
        return h.fromJuCollection(targetColl, types.target());
    }


    private static class Equiv {
        // setup: helper collections to build up the equivalence relationship between collection elements
        final Set<Object> sourceWithoutTarget = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
        final Set<Object> targetWithoutSource = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());

        final IdentityHashMap<Object, Object> equiv = new IdentityHashMap<Object, Object>();

        public Equiv(Collection<Object> sourceColl, Collection<Object> targetColl, AQualifiedSourceAndTargetType types, AIdentifierExtractor identifierExtractor) {
            targetWithoutSource.addAll(targetColl);

            // 'types' for calculating target element identifiers in term of the target type
            final AQualifiedSourceAndTargetType targetTypes = new AQualifiedSourceAndTargetType(types.targetType, types.targetQualifier, types.targetType, types.targetQualifier);

            // iterate through all source elements and sort source / target elements according to identifier equality
            for(Object s: sourceColl) {
                final Object sourceIdent = identifierExtractor.uniqueIdentifier(s, types);
                final AOption<Object> equivTarget = findTarget(targetColl, sourceIdent, identifierExtractor, targetTypes);
                if (equivTarget.isDefined ()) {
                    targetWithoutSource.remove (equivTarget.get());
                    equiv.put (s, equivTarget.get());
                }
                else {
                    sourceWithoutTarget.add(s);
                }
            }
        }

        private static AOption<Object> findTarget(Collection<Object> targetColl, Object ident, AIdentifierExtractor identifierExtractor, AQualifiedSourceAndTargetType targetTypes) {
            for(Object candidate: targetColl) {
                if (AEquality.EQUALS.equals (ident, identifierExtractor.uniqueIdentifier(candidate, targetTypes))) {
                    return AOption.some(candidate);
                }
            }
            return AOption.none();
        }

        @Override
        public String toString() {
            return "Equiv{" + sourceWithoutTarget + " / " + targetWithoutSource + " | " + equiv + "}";
        }
    }

    @Override public void diff(ADiffBuilder diff, Object sourceOld, Object sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends ACollectionHelper> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
        final ACollectionHelper h = worker.getHelpers();

        final Collection<Object> sourceOldColl = h.asJuCollection(sourceOld, types.source());
        final Collection<Object> sourceNewColl = h.asJuCollection(sourceNew, types.target());

        final AQualifiedSourceAndTargetType elementTypes = new AQualifiedSourceAndTargetType(h.elementType(types.sourceType), types.sourceQualifier, h.elementType(types.targetType), types.targetQualifier);
        final AQualifiedSourceAndTargetType sourceTypes = new AQualifiedSourceAndTargetType(types.sourceType, types.sourceQualifier, types.sourceType, types.sourceQualifier);

        final Equiv equiv = new Equiv(sourceOldColl, sourceNewColl, sourceTypes, worker.getIdentifierExtractor());

        // elements present in both old and new collection: no difference as far as the collection is concerned, recursive diff
        for(Map.Entry<Object,Object> e: equiv.equiv.entrySet()) {
            final APath elPath = ACollectionMappingTools.elementPath(path, worker.getIdentifierExtractor().uniqueIdentifier(e.getKey(), elementTypes));
            worker.diff(elPath, e.getKey(), e.getValue(), elementTypes, contextOld, contextNew, isDerived);
        }

        // elements only in the new collection: 'added' diff element + recursive diff with 'derived' = true
        for(Object newEl: equiv.targetWithoutSource) {
            final APath elPath = ACollectionMappingTools.elementPath(path, worker.getIdentifierExtractor().uniqueIdentifier(newEl, elementTypes));
            worker.diff(elPath, null, newEl, elementTypes, contextOld, contextNew, isDerived);
        }

        // elements only in the old collection: 'removed' diff element + recursive diff with 'derived' = true
        for(Object oldEl: equiv.sourceWithoutTarget) {
            final APath elPath = ACollectionMappingTools.elementPath(path, worker.getIdentifierExtractor().uniqueIdentifier(oldEl, elementTypes));
            worker.diff(elPath, oldEl, null, elementTypes, contextOld, contextNew, isDerived);
        }
    }
}
