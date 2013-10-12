package com.ajjpj.amapper.javabean;


import com.ajjpj.amapper.core.diff.ADiff;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.core.tpe.AType;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * @author arno
 */
public interface JavaBeanMapper {
    <T> T map(Object source, AType sourceType, AQualifier sourceQualifier, T target, AType targetType, AQualifier targetQualifier) throws Exception;
    <T> T map(Object source, AType sourceType, T target, AType targetType) throws Exception;
    <T> T map(Object source, Class<?> sourceClass, Class<?> sourceElementClass, T target, Class<T> targetClass, Class<?> targetElementClass) throws Exception;
    <T> T map(Object source, Class<?> sourceClass, T target, Class<T> targetClass) throws Exception;
    <T> T map(Object source, Class<?> sourceClass, Class<T> targetClass) throws Exception;
    <T> T map(Object source, Class<T> targetClass) throws Exception;

    <T> T map(Object source, T target) throws Exception;

    <S,T> List<T> mapList(Collection<?> source, Class<S> sourceElementClass, List<T> target, Class<T> targetElementClass) throws Exception;
    <S,T> List<T> mapList(Collection<?> source, Class<S> sourceElementClass, Class<T> targetElementClass) throws Exception;

    <S,T> Set<T> mapSet(Collection<?> source, Class<S> sourceElementClass, Set<T> target, Class<T> targetElementClass) throws Exception;
    <S,T> Set<T> mapSet(Collection<?> source, Class<S> sourceElementClass, Class<T> targetElementClass) throws Exception;


    ADiff diff(Object sourceOld, Object sourceNew, AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) throws Exception;
    ADiff diff(Object sourceOld, Object sourceNew, AType sourceType,                             AType targetType) throws Exception;
    ADiff diff(Object sourceOld, Object sourceNew, Class<?> sourceType,                          Class<?> targetType) throws Exception;

    ADiff diffList(List<?> sourceOld, List<?> sourceNew, Class<?> sourceElementType, Class<?> targetElementType) throws Exception;
    ADiff diffSet(Set<?> sourceOld, Set<?> sourceNew, Class<?> sourceElementType, Class<?> targetElementType) throws Exception;
}
