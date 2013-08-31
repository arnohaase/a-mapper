package com.ajjpj.amapper.javabean.japi;

import com.ajjpj.amapper.core.AQualifier;
import com.ajjpj.amapper.javabean.JavaBeanType;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * @author arno
 */
public interface JavaBeanMapper {
    <T> T map(Object source, JavaBeanType<?> sourceType, AQualifier sourceQualifier, T target, JavaBeanType<T> targetType, AQualifier targetQualifier);
    <T> T map(Object source, Class<?> sourceClass, Class<?> sourceElementClass, T target, Class<T> targetClass, Class<?> targetElementClass);
    <T> T map(Object source, Class<?> sourceClass, T target, Class<T> targetClass);
    <T> T map(Object source, Class<?> sourceClass, Class<T> targetClass);
    <T> T map(Object source, Class<T> targetClass);

    <T> T map(Object source, T target);

    <S,T> List<T> mapList(Collection<?> source, Class<S> sourceElementClass, List<T> target, Class<T> targetElementClass);
    <S,T> List<T> mapList(Collection<?> source, Class<S> sourceElementClass, Class<T> targetElementClass);

    <S,T> Set<T> mapSet(Collection<?> source, Class<S> sourceElementClass, Set<T> target, Class<T> targetElementClass);
    <S,T> Set<T> mapSet(Collection<?> source, Class<S> sourceElementClass, Class<T> targetElementClass);

    <T> T map(Object source, JavaBeanType<?> sourceType, T target, JavaBeanType<T> targetType); //TODO verify Java API for creating JavaBeanType
}
