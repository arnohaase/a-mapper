package com.ajjpj.amapper.javabean.builder;

import com.ajjpj.amapper.javabean.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean.JavaBeanType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.builder.qualifier.AAnnotationBasedQualifierExtractor;
import com.ajjpj.amapper.javabean.builder.qualifier.AQualifierExtractor;
import com.ajjpj.amapper.javabean.propbased.*;
import com.ajjpj.amapper.javabean.propbased.accessors.APropertyAccessor;

import java.util.*;

/**
 * This class is actually a builder for Java Bean mapping defs. It is part of the most widely-used public API however,
 *  and therefore a simple and intuitive name was chosen.<p />
 *
 * To use it, create an instance by calling the static create() method with 'source' and 'target' types. 'Source' and
 *  'target' are just labels to readily identify the two sides of the mapping - a single instance of JavaBeanMapping
 *  keeps track of mappings in both directions, and registering it with a JavaBeanMapperBuilder instance registers
 *  mappings in both directions.<p />
 *
 * By default, no properties are registered for mapping. If you want properties with matching names to be mapped,
 *  call <code>withMatchingPropsMappings()</code>.
 *
 * @author arno
 */
public class JavaBeanMapping<S,T, H extends JavaBeanMappingHelper> { //TODO flag to create only a unidirectional mapping?
    private final Class<S> sourceCls;
    private final Class<T> targetCls;

    private final AIsDeferredStrategy deferredStrategy;
    private final AQualifierExtractor qualifierExtractor;

    private List<APartialBeanMapping<S,T,?>> forwardProps  = new ArrayList<APartialBeanMapping<S, T, ?>>();
    private List<APartialBeanMapping<T,S,?>> backwardProps = new ArrayList<APartialBeanMapping<T, S, ?>>();


    public static <S,T> JavaBeanMapping<S,T, JavaBeanMappingHelper> create(Class<S> sourceCls, Class<T> targetCls) {
        return create(sourceCls, targetCls, AIsDeferredStrategy.ANNOTATION_BASED, AAnnotationBasedQualifierExtractor.INSTANCE);
    }

    public static <S,T> JavaBeanMapping<S,T, JavaBeanMappingHelper> create(Class<S> sourceCls, Class<T> targetCls, AIsDeferredStrategy deferredStrategy, AQualifierExtractor qualifierExtractor) {
        return new JavaBeanMapping<S, T, JavaBeanMappingHelper>(sourceCls, targetCls, deferredStrategy, qualifierExtractor);
    }

    public JavaBeanMapping(Class<S> sourceCls, Class<T> targetCls, AIsDeferredStrategy deferredStrategy, AQualifierExtractor qualifierExtractor) {
        this.sourceCls = sourceCls;
        this.targetCls = targetCls;
        this.deferredStrategy = deferredStrategy;
        this.qualifierExtractor = qualifierExtractor;
    }

    public JavaBeanMapping<S,T,H> withMatchingPropsMappings() throws Exception {
        return withMatchingPropsMappings(true);
    }

    /**
     * This method compares all Java Bean properties of source and target class, registering a bidirectional mapping if
     *  a source and target property have the same name. <p />
     *
     * This heuristic is by no means fail-safe. There may happen to be properties that have the same name but should or can not
     *  be mapped. Or some properties should only be mapped one-way. <p />
     *
     * So be aware of this method's limitations. That said, it is often a good starting point - call it early, and then
     *  customize the mapping using the more detailed methods of <code>JavaBeanMapping</code>.<p />
     *
     * @param removeReadOnly The Java Bean standard says that a bean property is read-only if there is no setter for it. If
     *                        'removeReadOnly' is set to 'true', mappings to these read-only properties are not registered. That
     *                        is the default if you leave this parameter out.<p />
     *                       In some domains however it may not be necessary to modify properties because there is always
     *                        an existing object to modify. There may e.g. not be a setter for a collection because that
     *                        collections is always modified using 'add' and 'remove'. If you want mappings to be automatically
     *                        registered to those 'read-only' properties as well, pass 'false' for this parameter.
     */
    public JavaBeanMapping<S,T,H> withMatchingPropsMappings(boolean removeReadOnly) throws Exception {
        final Map<String, APropertyAccessor> sourceProps = new JavaBeanSupport(deferredStrategy, qualifierExtractor).getAllProperties(sourceCls);
        final Map<String, APropertyAccessor> targetProps = new JavaBeanSupport(deferredStrategy, qualifierExtractor).getAllProperties(targetCls);

        final Set<String> shared = new HashSet<String>(sourceProps.keySet());
        shared.retainAll(targetProps.keySet());

        for(String propName: shared) {
            final APropertyAccessor sourceProp = sourceProps.get(propName);
            final APropertyAccessor targetProp = targetProps.get(propName);

            if(targetProp.isWritable() || !removeReadOnly) {
                forwardProps. add(new ASourceAndTargetProp<S, T>(sourceProp, targetProp));
            }
            if(sourceProp.isWritable() || !removeReadOnly) {
                backwardProps.add(new ASourceAndTargetProp<T, S>(targetProp, sourceProp));
            }
        }

        return this;
    }

    public JavaBeanMapping<S,T,H> addMapping (String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return addMapping(sourceExpression, sourceClass, targetExpression, targetClass, false); }
    public JavaBeanMapping<S,T,H> addMapping (String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return addMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T,H> addMapping (String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return addMapping(sourceExpression, sourceClass, sourceElementClass, targetExpression, targetClass, targetElementClass, false); }
    public JavaBeanMapping<S,T,H> addMapping (String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return addMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T,H> addMapping (String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        final APropertyAccessor sourceAccessor = new ABeanExpressionParser(qualifierExtractor).parse(sourceCls, sourceExpression, sourceType, isDeferred);
        final APropertyAccessor targetAccessor = new ABeanExpressionParser(qualifierExtractor).parse(targetCls, targetExpression, targetType, isDeferred);

        forwardProps.add(new ASourceAndTargetProp<S, T>(sourceAccessor, targetAccessor));
        backwardProps.add(new ASourceAndTargetProp<T, S>(targetAccessor, sourceAccessor));
        return this;
    }

    public JavaBeanMapping<S,T,H> overrideMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return overrideMapping(sourceExpression, sourceClass, targetExpression, targetClass, false); }
    public JavaBeanMapping<S,T,H> overrideMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return overrideMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T,H> overrideMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return overrideMapping(sourceExpression, sourceClass, sourceElementClass, targetExpression, targetClass, targetElementClass, false); }
    public JavaBeanMapping<S,T,H> overrideMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return overrideMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T,H> overrideMapping(String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        removeMappingForSourceProp(sourceExpression);
        removeMappingForTargetProp(targetExpression);
        addMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred);
        return this;
    }

    public JavaBeanMapping<S,T,H> addOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return addOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), false); }
    public JavaBeanMapping<S,T,H> addOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return addOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T,H> addOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return addOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), false); }
    public JavaBeanMapping<S,T,H> addOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return addOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T,H> addOneWayMapping(String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        final APropertyAccessor sourceAccessor = new ABeanExpressionParser(qualifierExtractor).parse(sourceCls, sourceExpression, sourceType, isDeferred);
        final APropertyAccessor targetAccessor = new ABeanExpressionParser(qualifierExtractor).parse(targetCls, targetExpression, targetType, isDeferred);

        forwardProps.add(new ASourceAndTargetProp<S, T>(sourceAccessor, targetAccessor));
        return this;
    }

    public JavaBeanMapping<S,T,H> addBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return addBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), false); }
    public JavaBeanMapping<S,T,H> addBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return addBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T,H> addBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return addBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), false); }
    public JavaBeanMapping<S,T,H> addBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return addBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T,H> addBackwardsOneWayMapping(String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        final APropertyAccessor sourceAccessor = new ABeanExpressionParser(qualifierExtractor).parse(sourceCls, sourceExpression, sourceType, isDeferred);
        final APropertyAccessor targetAccessor = new ABeanExpressionParser(qualifierExtractor).parse(targetCls, targetExpression, targetType, isDeferred);

        backwardProps.add(new ASourceAndTargetProp<T, S>(targetAccessor, sourceAccessor));
        return this;
    }

    public JavaBeanMapping<S,T,H> removeMappingForSourceProp(String expr) {
        for(Iterator<APartialBeanMapping<S,T,?>> iter=forwardProps.iterator(); iter.hasNext(); ) {
            if(iter.next().getSourceName().equals(expr)) {
                iter.remove();
            }
        }
        for(Iterator<APartialBeanMapping<T,S,?>> iter=backwardProps.iterator(); iter.hasNext(); ) {
            if(iter.next().getTargetName().equals(expr)) {
                iter.remove();
            }
        }
        return this;
    }

    public JavaBeanMapping<S,T,H> removeMappingForTargetProp(String expr) {
        for(Iterator<APartialBeanMapping<S,T,?>> iter=forwardProps.iterator(); iter.hasNext(); ) {
            if(iter.next().getTargetName().equals(expr)) {
                iter.remove();
            }
        }
        for(Iterator<APartialBeanMapping<T,S,?>> iter=backwardProps.iterator(); iter.hasNext(); ) {
            if(iter.next().getSourceName().equals(expr)) {
                iter.remove();
            }
        }
        return this;
    }

    public JavaBeanMapping<S,T,H> overrideWithOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return overrideWithOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), false); }
    public JavaBeanMapping<S,T,H> overrideWithOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return overrideWithOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T,H> overrideWithOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return overrideWithOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), false); }
    public JavaBeanMapping<S,T,H> overrideWithOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return overrideWithOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T,H> overrideWithOneWayMapping(String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        removeMappingForSourceProp(sourceExpression);
        removeMappingForTargetProp(targetExpression);
        return addOneWayMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred);
    }

    public JavaBeanMapping<S,T,H> overrideWithBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return overrideWithBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), false); }
    public JavaBeanMapping<S,T,H> overrideWithBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return overrideWithBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T,H> overrideWithBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return overrideWithBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), false); }
    public JavaBeanMapping<S,T,H> overrideWithBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return overrideWithBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T,H> overrideWithBackwardsOneWayMapping(String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        removeMappingForSourceProp(sourceExpression);
        removeMappingForTargetProp(targetExpression);
        return addBackwardsOneWayMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred);
    }

    public JavaBeanMapping<S,T,H> makeOneWay(String sourceExpression) {
        for(Iterator<APartialBeanMapping<T,S,?>> iter=backwardProps.iterator(); iter.hasNext(); ) {
            if(iter.next().getTargetName().equals(sourceExpression)) {
                iter.remove();
            }
        }
        return this;
    }

    public JavaBeanMapping<S,T,H> makeBackwardsOneWay(String targetExpression) {
        for(Iterator<APartialBeanMapping<S,T,?>> iter=forwardProps.iterator(); iter.hasNext(); ) {
            if(iter.next().getTargetName().equals(targetExpression)) {
                iter.remove();
            }
        }
        return this;
    }

    public JavaBeanMapping<S,T,H> addForwardSpecialMapping(APartialBeanMapping<S,T,?> mapping) {
        forwardProps.add(mapping);
        return this;
    }

    public JavaBeanMapping<S,T,H> addBackwardSpecialMapping(APartialBeanMapping<T,S,?> mapping) {
        backwardProps.add(mapping);
        return this;
    }

    @SuppressWarnings("unchecked")
    public JavaBeanMapping<S,T,H> withForwardGuardBySourceExpression(String sourceExpression, AGuardCondition<S,T,?> guard) {
        final List<APartialBeanMapping<S, T, Object>> replacements = new ArrayList<APartialBeanMapping<S, T, Object>>();

        for(Iterator<APartialBeanMapping<S, T, ?>> iter = forwardProps.iterator(); iter.hasNext(); ) {
            final APartialBeanMapping<S, T, ?> cur = iter.next();
            if(cur.getSourceName().equals(sourceExpression)) {
                iter.remove();
                replacements.add(new AGuardedPartialMapping<S, T, Object>((APartialBeanMapping) cur, (AGuardCondition) guard));
            }
        }

        forwardProps.addAll(replacements);
        return this;
    }

    @SuppressWarnings("unchecked")
    public JavaBeanMapping<S,T,H> withForwardGuardByTargetExpression(String targetExpression, AGuardCondition<S,T,?> guard) {
        final List<APartialBeanMapping<S, T, Object>> replacements = new ArrayList<APartialBeanMapping<S, T, Object>>();

        for(Iterator<APartialBeanMapping<S, T, ?>> iter = forwardProps.iterator(); iter.hasNext(); ) {
            final APartialBeanMapping<S, T, ?> cur = iter.next();
            if(cur.getTargetName().equals(targetExpression)) {
                iter.remove();
                replacements.add(new AGuardedPartialMapping<S, T, Object>((APartialBeanMapping) cur, (AGuardCondition) guard));
            }
        }

        forwardProps.addAll(replacements);
        return this;
    }

    @SuppressWarnings("unchecked")
    public JavaBeanMapping<S,T,H> withBackwardGuardBySourceExpression(String sourceExpression, AGuardCondition<T,S,?> guard) {
        final List<APartialBeanMapping<T, S, Object>> replacements = new ArrayList<APartialBeanMapping<T, S, Object>>();

        for(Iterator<APartialBeanMapping<T, S, ?>> iter = backwardProps.iterator(); iter.hasNext(); ) {
            final APartialBeanMapping<T, S, ?> cur = iter.next();
            if(cur.getSourceName().equals(sourceExpression)) {
                iter.remove();
                replacements.add(new AGuardedPartialMapping<T, S, Object>((APartialBeanMapping) cur, (AGuardCondition) guard));
            }
        }

        backwardProps.addAll(replacements);
        return this;
    }

    @SuppressWarnings("unchecked")
    public JavaBeanMapping<S,T,H> withBackwardGuardByTargetExpression(String targetExpression, AGuardCondition<T,S,?> guard) {
        final List<APartialBeanMapping<T, S, Object>> replacements = new ArrayList<APartialBeanMapping<T, S, Object>>();

        for(Iterator<APartialBeanMapping<T, S, ?>> iter = backwardProps.iterator(); iter.hasNext(); ) {
            final APartialBeanMapping<T, S, ?> cur = iter.next();
            if(cur.getTargetName().equals(targetExpression)) {
                iter.remove();
                replacements.add(new AGuardedPartialMapping<T, S, Object>((APartialBeanMapping) cur, (AGuardCondition) guard));
            }
        }

        backwardProps.addAll(replacements);
        return this;
    }

    public Class<S> getSourceClass() {
        return sourceCls;
    }

    public Class<T> getTargetClass() {
        return targetCls;
    }

    @SuppressWarnings("unchecked")
    public APropertyBasedObjectMappingDef<S,T,H> build() {
        return new APropertyBasedObjectMappingDef(sourceCls, targetCls, forwardProps);
    }
    @SuppressWarnings("unchecked")
    public APropertyBasedObjectMappingDef<T,S,H> buildBackward() {
        return new APropertyBasedObjectMappingDef(targetCls, sourceCls, backwardProps);
    }
}

