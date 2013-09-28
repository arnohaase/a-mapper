package com.ajjpj.amapper.javabean2.builder;

import com.ajjpj.amapper.javabean2.JavaBeanType;
import com.ajjpj.amapper.javabean2.JavaBeanTypes;
import com.ajjpj.amapper.javabean2.builder.qualifier.AAnnotationBasedQualifierExtractor;
import com.ajjpj.amapper.javabean2.builder.qualifier.AQualifierExtractor;
import com.ajjpj.amapper.javabean2.propbased.APartialBeanMapping;
import com.ajjpj.amapper.javabean2.propbased.APropertyBasedObjectMappingDef;
import com.ajjpj.amapper.javabean2.propbased.ASourceAndTargetProp;
import com.ajjpj.amapper.javabean2.propbased.accessors.APropertyAccessor;

import java.util.*;

/**
 * @author arno
 */
public class JavaBeanMapping<S,T> {
    private final Class<S> sourceCls;
    private final Class<T> targetCls;

    private final AIsDeferredStrategy deferredStrategy;
    private final AQualifierExtractor qualifierExtractor;

    private List<APartialBeanMapping<S,T,?>> forwardProps  = new ArrayList<APartialBeanMapping<S, T, ?>>();
    private List<APartialBeanMapping<T,S,?>> backwardProps = new ArrayList<APartialBeanMapping<T, S, ?>>();


    public static <S,T> JavaBeanMapping<S,T> create(Class<S> sourceCls, Class<T> targetCls) {
        return create(sourceCls, targetCls, AIsDeferredStrategy.ANNOTATION_BASED, AAnnotationBasedQualifierExtractor.INSTANCE);
    }

    public static <S,T> JavaBeanMapping<S,T> create(Class<S> sourceCls, Class<T> targetCls, AIsDeferredStrategy deferredStrategy, AQualifierExtractor qualifierExtractor) {
        return new JavaBeanMapping<S, T>(sourceCls, targetCls, deferredStrategy, qualifierExtractor);
    }

    public JavaBeanMapping(Class<S> sourceCls, Class<T> targetCls, AIsDeferredStrategy deferredStrategy, AQualifierExtractor qualifierExtractor) {
        this.sourceCls = sourceCls;
        this.targetCls = targetCls;
        this.deferredStrategy = deferredStrategy;
        this.qualifierExtractor = qualifierExtractor;
    }

    public JavaBeanMapping<S,T> withMatchingPropsMappings() throws Exception {
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
    public JavaBeanMapping<S,T> withMatchingPropsMappings(boolean removeReadOnly) throws Exception {
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
            if(sourceProp.isWritable() && !removeReadOnly) {
                backwardProps.add(new ASourceAndTargetProp<T, S>(targetProp, sourceProp));
            }
        }
        return this;
    }

    public JavaBeanMapping<S,T> addMapping (String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return addMapping(sourceExpression, sourceClass, targetExpression, targetClass, false); }
    public JavaBeanMapping<S,T> addMapping (String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return addMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T> addMapping (String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return addMapping(sourceExpression, sourceClass, sourceElementClass, targetExpression, targetClass, targetElementClass, false); }
    public JavaBeanMapping<S,T> addMapping (String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return addMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T> addMapping (String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        final APropertyAccessor sourceAccessor = new ABeanExpressionParser(qualifierExtractor).parse(sourceCls, sourceExpression, sourceType, isDeferred);
        final APropertyAccessor targetAccessor = new ABeanExpressionParser(qualifierExtractor).parse(targetCls, targetExpression, targetType, isDeferred);

        forwardProps.add(new ASourceAndTargetProp<S, T>(sourceAccessor, targetAccessor));
        backwardProps.add(new ASourceAndTargetProp<T, S>(targetAccessor, sourceAccessor));
        return this;
    }

    public JavaBeanMapping<S,T> overrideMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return overrideMapping(sourceExpression, sourceClass, targetExpression, targetClass, false); }
    public JavaBeanMapping<S,T> overrideMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return overrideMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T> overrideMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return overrideMapping(sourceExpression, sourceClass, sourceElementClass, targetExpression, targetClass, targetElementClass, false); }
    public JavaBeanMapping<S,T> overrideMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return overrideMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T> overrideMapping(String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        removeMappingForSourceProp(sourceExpression);
        removeMappingForTargetProp(targetExpression);
        addMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred);
        return this;
    }

    public JavaBeanMapping<S,T> addOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return addOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), false); }
    public JavaBeanMapping<S,T> addOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return addOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T> addOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return addOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), false); }
    public JavaBeanMapping<S,T> addOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return addOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T> addOneWayMapping(String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        final APropertyAccessor sourceAccessor = new ABeanExpressionParser(qualifierExtractor).parse(sourceCls, sourceExpression, sourceType, isDeferred);
        final APropertyAccessor targetAccessor = new ABeanExpressionParser(qualifierExtractor).parse(targetCls, targetExpression, targetType, isDeferred);

        forwardProps.add(new ASourceAndTargetProp<S, T>(sourceAccessor, targetAccessor));
        return this;
    }

    public JavaBeanMapping<S,T> addBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return addBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), false); }
    public JavaBeanMapping<S,T> addBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return addBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T> addBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return addBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), false); }
    public JavaBeanMapping<S,T> addBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return addBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T> addBackwardsOneWayMapping(String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        final APropertyAccessor sourceAccessor = new ABeanExpressionParser(qualifierExtractor).parse(sourceCls, sourceExpression, sourceType, isDeferred);
        final APropertyAccessor targetAccessor = new ABeanExpressionParser(qualifierExtractor).parse(targetCls, targetExpression, targetType, isDeferred);

        backwardProps.add(new ASourceAndTargetProp<T, S>(targetAccessor, sourceAccessor));
        return this;
    }

    public JavaBeanMapping<S,T> removeMappingForSourceProp(String expr) {
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

    public JavaBeanMapping<S,T> removeMappingForTargetProp(String expr) {
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

    public JavaBeanMapping<S,T> overrideWithOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return overrideWithOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), false); }
    public JavaBeanMapping<S,T> overrideWithOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return overrideWithOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T> overrideWithOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return overrideWithOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), false); }
    public JavaBeanMapping<S,T> overrideWithOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return overrideWithOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T> overrideWithOneWayMapping(String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        removeMappingForSourceProp(sourceExpression);
        removeMappingForTargetProp(targetExpression);
        return addOneWayMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred);
    }

    public JavaBeanMapping<S,T> overrideWithBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass                                                 ) throws Exception { return overrideWithBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), false); }
    public JavaBeanMapping<S,T> overrideWithBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass,                              String targetExpression, Class<?> targetClass,                              boolean isDeferred) throws Exception { return overrideWithBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass), targetExpression, JavaBeanTypes.create(targetClass), isDeferred); }
    public JavaBeanMapping<S,T> overrideWithBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass                    ) throws Exception { return overrideWithBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), false); }
    public JavaBeanMapping<S,T> overrideWithBackwardsOneWayMapping(String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, String targetExpression, Class<?> targetClass, Class<?> targetElementClass, boolean isDeferred) throws Exception { return overrideWithBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceClass, sourceElementClass), targetExpression, JavaBeanTypes.create(targetClass, targetElementClass), isDeferred); }

    public JavaBeanMapping<S,T> overrideWithBackwardsOneWayMapping(String sourceExpression, JavaBeanType<?> sourceType, String targetExpression, JavaBeanType<?> targetType, boolean isDeferred) throws Exception {
        removeMappingForSourceProp(sourceExpression);
        removeMappingForTargetProp(targetExpression);
        return addBackwardsOneWayMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred);
    }


    public JavaBeanMapping<S,T> makeOneWay(String sourceExpression) {
        for(Iterator<APartialBeanMapping<T,S,?>> iter=backwardProps.iterator(); iter.hasNext(); ) {
            if(iter.next().getTargetName().equals(sourceExpression)) {
                iter.remove();
            }
        }
        return this;
    }

    public JavaBeanMapping<S,T> makeBackwardsOneWay(String targetExpression) {
        for(Iterator<APartialBeanMapping<S,T,?>> iter=forwardProps.iterator(); iter.hasNext(); ) {
            if(iter.next().getTargetName().equals(targetExpression)) {
                iter.remove();
            }
        }
        return this;
    }

    public JavaBeanMapping<S,T> addForwardSpecialMapping(APartialBeanMapping<S,T,?> mapping) {
        forwardProps.add(mapping);
        return this;
    }

    public JavaBeanMapping<S,T> addBackwardSpecialMapping(APartialBeanMapping<T,S,?> mapping) {
        backwardProps.add(mapping);
        return this;
    }

    public APropertyBasedObjectMappingDef build() {
        return new APropertyBasedObjectMappingDef(sourceCls, targetCls, forwardProps);
    }
    public APropertyBasedObjectMappingDef buildBackward() {
        return new APropertyBasedObjectMappingDef(targetCls, sourceCls, backwardProps);
    }
}

//        def withForwardGuardBySourceExpression(sourceExpr: String, shouldMap: ShouldMap[S,T]) = {
//        forwardProps = forwardProps.map (_ match {
//        case p if p.sourceName == sourceExpr => new GuardedPartialMapping(p, shouldMap)
//        case p => p
//        })
//        this
//        }
//        def withForwardGuardByTargetExpression(targetExpr: String, shouldMap: ShouldMap[S,T]) = {
//        forwardProps = forwardProps.map (_ match {
//        case p if p.targetName == targetExpr => new GuardedPartialMapping(p, shouldMap)
//        case p => p
//        })
//        this
//        }
//        def withBackwardGuardBySourceExpression(sourceExpr: String, shouldMap: ShouldMap[T,S]) = {
//        backwardProps = backwardProps.map (_ match {
//        case p if p.sourceName == sourceExpr => new GuardedPartialMapping(p, shouldMap)
//        case p => p
//        })
//        this
//        }
//        def withBackwardGuardByTargetExpression(targetExpr: String, shouldMap: ShouldMap[T,S]) = {
//        backwardProps = backwardProps.map (_ match {
//        case p if p.targetName == targetExpr => new GuardedPartialMapping(p, shouldMap)
//        case p => p
//        })
//        this
//        }
