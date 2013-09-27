package com.ajjpj.amapper.javabean2.builder;

import com.ajjpj.amapper.core2.exclog.AMapperLogger;
import com.ajjpj.amapper.javabean2.JavaBeanType;
import com.ajjpj.amapper.javabean2.JavaBeanTypes;
import com.ajjpj.amapper.javabean2.builder.qualifier.AQualifierExtractor;
import com.ajjpj.amapper.javabean2.propbased.APartialBeanMapping;
import com.ajjpj.amapper.javabean2.propbased.ASourceAndTargetProp;
import com.ajjpj.amapper.javabean2.propbased.accessors.APropertyAccessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author arno
 */
public class JavaBeanMapping<S,T> {
    private final Class<S> sourceCls;
    private final Class<T> targetCls;

    private final AIsDeferredStrategy deferredStrategy;
    private final AMapperLogger logger;
    private final AQualifierExtractor qualifierExtractor;

    private List<APartialBeanMapping<S,T,?>> forwardProps  = new ArrayList<APartialBeanMapping<S, T, ?>>();
    private List<APartialBeanMapping<T,S,?>> backwardProps = new ArrayList<APartialBeanMapping<T, S, ?>>();


    public JavaBeanMapping(Class<S> sourceCls, Class<T> targetCls, AIsDeferredStrategy deferredStrategy, AMapperLogger logger, AQualifierExtractor qualifierExtractor) {
        this.sourceCls = sourceCls;
        this.targetCls = targetCls;
        this.deferredStrategy = deferredStrategy;
        this.logger = logger;
        this.qualifierExtractor = qualifierExtractor;
    }

    public JavaBeanMapping<S,T> withMatchingPropsMappings() {
        //TODO implement this
//        val sharedProps = PropertyAccessor.sharedProperties(sourceCls, targetCls, isPropDeferred, logger, qualifierExtractor)
//        forwardProps  ++= sharedProps.               filter(p => p.sourceProp.isReadable && p.targetProp.isWritable).asInstanceOf[Iterable[PartialMapping[S,T]]]
//        backwardProps ++= sharedProps.map(_.reverse).filter(p => p.sourceProp.isReadable && p.targetProp.isWritable).asInstanceOf[Iterable[PartialMapping[T,S]]]
//        this
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
//
//        //TODO log warning if there is no such mapping
//
//        def build = PropertyBasedObjectMappingDef[S,T](forwardProps)
//        def buildBackward = PropertyBasedObjectMappingDef[T,S](backwardProps)
//        }
//
//        object JavaBeanMapping {
//        def create[S<:AnyRef, T<:AnyRef] (sourceClass: Class[S], targetClass: Class[T]): JavaBeanMapping[S,T]                                         = create(sourceClass, targetClass, AMapperLogger.defaultLogger)
//        def create[S<:AnyRef, T<:AnyRef] (sourceClass: Class[S], targetClass: Class[T], logger: AMapperLogger): JavaBeanMapping[S,T]                  = create(sourceClass, targetClass, DefaultIsDeferredStrategy, logger, DefaultQualifierExtractor)
//        def create[S<:AnyRef, T<:AnyRef] (sourceClass: Class[S], targetClass: Class[T], isDeferredStrategy: IsDeferredStrategy): JavaBeanMapping[S,T] = create(sourceClass, targetClass, isDeferredStrategy, AMapperLogger.defaultLogger, DefaultQualifierExtractor)
//
//        def create[S<:AnyRef, T<:AnyRef] (sourceClass: Class[S], targetClass: Class[T], isPropDeferred: IsDeferredStrategy, logger: AMapperLogger, qualifierExtractor: QualifierExtractor): JavaBeanMapping[S,T] = {
//        implicit val st = ClassTag[S](sourceClass)
//        implicit val tt = ClassTag[T](targetClass)
//        new JavaBeanMapping[S,T](isPropDeferred, logger, qualifierExtractor)
//        }
//        }
//
////TODO test for write-only property
//
//
//
//
