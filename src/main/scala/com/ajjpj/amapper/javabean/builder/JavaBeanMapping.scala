package com.ajjpj.amapper.javabean.builder

import com.ajjpj.amapper.javabean.propbased._
import scala.reflect.ClassTag
import com.ajjpj.amapper.javabean.{JavaBeanTypes, JavaBeanType}
import com.ajjpj.amapper.core.AMapperLogger
import com.ajjpj.amapper.javabean.propbased.SourceAndTargetProp
import com.ajjpj.amapper.javabean.propbased.PropertyBasedObjectMappingDef

/**
 * @author arno
 */
class JavaBeanMapping[S<:AnyRef,T<:AnyRef](isPropDeferred: IsDeferredStrategy, logger: AMapperLogger, qualifierExtractor: QualifierExtractor)(implicit sourceTag: ClassTag[S], targetTag: ClassTag[T]) {
  type THIS = JavaBeanMapping[S,T]
  type Type = JavaBeanType[_<:AnyRef]
  type Cls = Class[_<:AnyRef]

  val sourceCls = sourceTag.runtimeClass.asInstanceOf[Class[S]]
  val targetCls = targetTag.runtimeClass.asInstanceOf[Class[T]]

  var forwardProps: List[PartialMapping[S,T]] = Nil
  var backwardProps: List[PartialMapping[T,S]] = Nil

  def withMatchingPropsMappings() = {
    val sharedProps = PropertyAccessor.sharedProperties(sourceCls, targetCls, isPropDeferred, logger, qualifierExtractor)
    forwardProps  ++= sharedProps.               filter(p => p.sourceProp.isReadable && p.targetProp.isWritable).asInstanceOf[Iterable[PartialMapping[S,T]]]
    backwardProps ++= sharedProps.map(_.reverse).filter(p => p.sourceProp.isReadable && p.targetProp.isWritable).asInstanceOf[Iterable[PartialMapping[T,S]]]
    this
  }

  def addMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls): THIS = addMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred=false)
  def addMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls, isDeferred: Boolean): THIS = addMapping(sourceExpression, JavaBeanTypes.create(sourceType), targetExpression, JavaBeanTypes.create(targetType), isDeferred)
  def addMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls): THIS = addMapping(sourceExpression, sourceType, sourceElementType, targetExpression, targetType, targetElementType, isDeferred=false)
  def addMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls, isDeferred: Boolean): THIS = addMapping(sourceExpression, JavaBeanTypes.create(sourceType, sourceElementType), targetExpression, JavaBeanTypes.create(targetType, targetElementType), isDeferred)

  def addMapping(sourceExpression: String, sourceType: Type, targetExpression: String, targetType: Type, isDeferred: Boolean): THIS = {
    val sourceAccessor = new AMapperExpressionParser(qualifierExtractor).parse(sourceCls, sourceExpression, sourceType, isDeferred)
    val targetAccessor = new AMapperExpressionParser(qualifierExtractor).parse(targetCls, targetExpression, sourceType, isDeferred)

    if(sourceAccessor.isReadable && targetAccessor.isWritable)
      forwardProps = SourceAndTargetProp[S,T](sourceAccessor, targetAccessor) :: forwardProps

    if(targetAccessor.isReadable && sourceAccessor.isWritable)
      backwardProps = SourceAndTargetProp[T,S](targetAccessor, sourceAccessor) :: backwardProps

    //TODO warn if neither direction matches

    this
  }

  def overrideMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls): THIS = overrideMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred=false)
  def overrideMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls, isDeferred: Boolean): THIS = overrideMapping(sourceExpression, JavaBeanTypes.create(sourceType), targetExpression, JavaBeanTypes.create(targetType), isDeferred)
  def overrideMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls): THIS = overrideMapping(sourceExpression, sourceType, sourceElementType, targetExpression, targetType, targetElementType, isDeferred=false)
  def overrideMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls, isDeferred: Boolean): THIS = overrideMapping(sourceExpression, JavaBeanTypes.create(sourceType, sourceElementType), targetExpression, JavaBeanTypes.create(targetType, targetElementType), isDeferred)

  def overrideMapping(sourceExpression: String, sourceType: Type, targetExpression: String, targetType: Type, isDeferred: Boolean): THIS = {
    removeMappingForSourceProp(sourceExpression)
    removeMappingForTargetProp(targetExpression)
    addMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred)
  }

  def addOneWayMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls): THIS = addOneWayMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred=false)
  def addOneWayMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls, isDeferred: Boolean): THIS = addOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceType), targetExpression, JavaBeanTypes.create(targetType), isDeferred)
  def addOneWayMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls): THIS = addOneWayMapping(sourceExpression, sourceType, sourceElementType, targetExpression, targetType, targetElementType, isDeferred=false)
  def addOneWayMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls, isDeferred: Boolean): THIS = addOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceType, sourceElementType), targetExpression, JavaBeanTypes.create(targetType, targetElementType), isDeferred)

  def addOneWayMapping(sourceExpression: String, sourceType: Type, targetExpression: String, targetType: Type, isDeferred: Boolean): THIS = {
    val sourceAccessor = new AMapperExpressionParser(qualifierExtractor).parse(sourceCls, sourceExpression, sourceType, isDeferred)
    val targetAccessor = new AMapperExpressionParser(qualifierExtractor).parse(targetCls, targetExpression, sourceType, isDeferred)

    if(sourceAccessor.isReadable && targetAccessor.isWritable)
      forwardProps = SourceAndTargetProp[S,T](sourceAccessor, targetAccessor) :: forwardProps
    else
      throw new IllegalArgumentException("source property must be readable and target property must be writable")

    this
  }

  def overrideWithOneWayMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls): THIS = overrideWithOneWayMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred=false)
  def overrideWithOneWayMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls, isDeferred: Boolean): THIS = overrideWithOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceType), targetExpression, JavaBeanTypes.create(targetType), isDeferred)
  def overrideWithOneWayMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls): THIS = overrideWithOneWayMapping(sourceExpression, sourceType, sourceElementType, targetExpression, targetType, targetElementType, isDeferred=false)
  def overrideWithOneWayMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls, isDeferred: Boolean): THIS = overrideWithOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceType, sourceElementType), targetExpression, JavaBeanTypes.create(targetType, targetElementType), isDeferred)

  def overrideWithOneWayMapping(sourceExpression: String, sourceType: Type, targetExpression: String, targetType: Type, isDeferred: Boolean): THIS = {
    removeMappingForSourceProp(sourceExpression)
    removeMappingForTargetProp(targetExpression)
    addOneWayMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred)
  }


  def addBackwardsOneWayMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls): THIS = addBackwardsOneWayMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred=false)
  def addBackwardsOneWayMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls, isDeferred: Boolean): THIS = addBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceType), targetExpression, JavaBeanTypes.create(targetType), isDeferred)
  def addBackwardsOneWayMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls): THIS = addBackwardsOneWayMapping(sourceExpression, sourceType, sourceElementType, targetExpression, targetType, targetElementType, isDeferred=false)
  def addBackwardsOneWayMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls, isDeferred: Boolean): THIS = addBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceType, sourceElementType), targetExpression, JavaBeanTypes.create(targetType, targetElementType), isDeferred)

  def addBackwardsOneWayMapping(sourceExpression: String, sourceType: Type, targetExpression: String, targetType: Type, isDeferred: Boolean): THIS = {
    val sourceAccessor = new AMapperExpressionParser(qualifierExtractor).parse(sourceCls, sourceExpression, sourceType, isDeferred)
    val targetAccessor = new AMapperExpressionParser(qualifierExtractor).parse(targetCls, targetExpression, sourceType, isDeferred)

    if(targetAccessor.isReadable && sourceAccessor.isWritable)
      backwardProps = SourceAndTargetProp[T,S](targetAccessor, sourceAccessor) :: backwardProps
    else
      throw new IllegalArgumentException("target property must be readable and source property must be writable")

    this
  }

  def overrideWithBackwardsOneWayMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls): THIS = overrideWithBackwardsOneWayMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred=false)
  def overrideWithBackwardsOneWayMapping(sourceExpression: String, sourceType: Cls, targetExpression: String, targetType: Cls, isDeferred: Boolean): THIS = overrideWithBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceType), targetExpression, JavaBeanTypes.create(targetType), isDeferred)
  def overrideWithBackwardsOneWayMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls): THIS = overrideWithBackwardsOneWayMapping(sourceExpression, sourceType, sourceElementType, targetExpression, targetType, targetElementType, isDeferred=false)
  def overrideWithBackwardsOneWayMapping(sourceExpression: String, sourceType: Cls, sourceElementType: Cls, targetExpression: String, targetType: Cls, targetElementType: Cls, isDeferred: Boolean): THIS = overrideWithBackwardsOneWayMapping(sourceExpression, JavaBeanTypes.create(sourceType, sourceElementType), targetExpression, JavaBeanTypes.create(targetType, targetElementType), isDeferred)

  def overrideWithBackwardsOneWayMapping(sourceExpression: String, sourceType: Type, targetExpression: String, targetType: Type, isDeferred: Boolean): THIS = {
    removeMappingForSourceProp(sourceExpression)
    removeMappingForTargetProp(targetExpression)
    addBackwardsOneWayMapping(sourceExpression, sourceType, targetExpression, targetType, isDeferred)
  }


  def removeMappingForSourceProp(expr: String) = {
    forwardProps = forwardProps.filterNot(_.sourceName == expr)
    backwardProps = backwardProps.filterNot(_.targetName == expr)
    this
  }

  def removeMappingForTargetProp(expr: String) = {
    forwardProps = forwardProps.filterNot(_.targetName == expr)
    backwardProps = backwardProps.filterNot(_.sourceName == expr)
    this
  }

  def makeOneWay(sourceExpr: String) = {
    backwardProps = backwardProps.filterNot(_.targetName == sourceExpr)
    this
  }
  def makeBackwardsOneWay(sourceExpr: String) = {
    forwardProps = forwardProps.filterNot(_.sourceName == sourceExpr)
    this
  }

  def withForwardSpecialMapping(partialMapping: PartialMapping[S,T]) = {
    forwardProps = partialMapping :: forwardProps
    this
  }
  def withBackwardSpecialMapping(partialMapping: PartialMapping[T,S]) = {
    backwardProps = partialMapping :: backwardProps
    this
  }

  def withForwardGuardBySourceExpression(sourceExpr: String, shouldMap: ShouldMap[S,T]) = {
    forwardProps = forwardProps.map (_ match {
      case p if p.sourceName == sourceExpr => new GuardedPartialMapping(p, shouldMap)
      case p => p
    })
    this
  }
  def withForwardGuardByTargetExpression(targetExpr: String, shouldMap: ShouldMap[S,T]) = {
    forwardProps = forwardProps.map (_ match {
      case p if p.targetName == targetExpr => new GuardedPartialMapping(p, shouldMap)
      case p => p
    })
    this
  }
  def withBackwardGuardBySourceExpression(sourceExpr: String, shouldMap: ShouldMap[T,S]) = {
    backwardProps = backwardProps.map (_ match {
      case p if p.sourceName == sourceExpr => new GuardedPartialMapping(p, shouldMap)
      case p => p
    })
    this
  }
  def withBackwardGuardByTargetExpression(targetExpr: String, shouldMap: ShouldMap[T,S]) = {
    backwardProps = backwardProps.map (_ match {
      case p if p.targetName == targetExpr => new GuardedPartialMapping(p, shouldMap)
      case p => p
    })
    this
  }

  //TODO log warning if there is no such mapping

  def build = PropertyBasedObjectMappingDef[S,T](forwardProps)
  def buildBackward = PropertyBasedObjectMappingDef[T,S](backwardProps)
}

object JavaBeanMapping {
  def create[S<:AnyRef, T<:AnyRef] (sourceClass: Class[S], targetClass: Class[T]): JavaBeanMapping[S,T]                                         = create(sourceClass, targetClass, AMapperLogger.defaultLogger)
  def create[S<:AnyRef, T<:AnyRef] (sourceClass: Class[S], targetClass: Class[T], logger: AMapperLogger): JavaBeanMapping[S,T]                  = create(sourceClass, targetClass, DefaultIsDeferredStrategy, logger, DefaultQualifierExtractor)
  def create[S<:AnyRef, T<:AnyRef] (sourceClass: Class[S], targetClass: Class[T], isDeferredStrategy: IsDeferredStrategy): JavaBeanMapping[S,T] = create(sourceClass, targetClass, isDeferredStrategy, AMapperLogger.defaultLogger, DefaultQualifierExtractor)

  def create[S<:AnyRef, T<:AnyRef] (sourceClass: Class[S], targetClass: Class[T], isPropDeferred: IsDeferredStrategy, logger: AMapperLogger, qualifierExtractor: QualifierExtractor): JavaBeanMapping[S,T] = {
    implicit val st = ClassTag[S](sourceClass)
    implicit val tt = ClassTag[T](targetClass)
    new JavaBeanMapping[S,T](isPropDeferred, logger, qualifierExtractor)
  }
}

//TODO test for write-only property




