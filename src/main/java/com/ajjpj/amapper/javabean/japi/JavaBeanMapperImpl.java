package com.ajjpj.amapper.javabean.japi;

import com.ajjpj.amapper.AMapper;
import com.ajjpj.amapper.core.AQualifier;
import com.ajjpj.amapper.javabean.JavaBeanType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author arno
 */
class JavaBeanMapperImpl implements JavaBeanMapper {
    private final AMapper inner;

    JavaBeanMapperImpl(AMapper inner) {
        this.inner = inner;
    }

    @Override
    public <T> T map(Object source, Class<?> sourceClass, Class<?> sourceElementClass, T target, Class<T> targetClass, Class<?> targetElementClass) {
        return map(source, JavaBeanTypes.create(sourceClass, sourceElementClass), target, JavaBeanTypes.create(targetClass, targetElementClass));
    }

    @Override
    public <T> T map(Object source, Class<?> sourceClass, T target, Class<T> targetClass) {
        return map (source, JavaBeanTypes.create(sourceClass), target, JavaBeanTypes.create(targetClass));
    }

    @Override
    public <T> T map(Object source, Class<?> sourceClass, Class<T> targetClass) {
        return map(source, sourceClass, null, targetClass);
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass) {
        if(source == null) {
            return null;
        }
        return map(source, source.getClass(), targetClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T map(Object source, T target) {
        if(source == null) {
            return null;
        }
        return map(source, source.getClass(), target, (Class<T>) target.getClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S, T> List<T> mapList(Collection<?> source, Class<S> sourceElementClass, List<T> target, Class<T> targetElementClass) {
        return map(source, JavaBeanTypes.create(Collection.class, sourceElementClass), target, JavaBeanTypes.create(List.class, targetElementClass));
    }

    @Override
    public <S,T> List<T> mapList(Collection<?> source, Class<S> sourceElementClass, Class<T> targetElementClass) {
        return mapList(source, sourceElementClass, null, targetElementClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S,T> Set<T> mapSet(Collection<?> source, Class<S> sourceElementClass, Set<T> target, Class<T> targetElementClass) {
        return map(source, JavaBeanTypes.create(Collection.class, sourceElementClass), target, JavaBeanTypes.create(Set.class, targetElementClass));
    }

    @Override
    public <S,T> Set<T> mapSet(Collection<?> source, Class<S> sourceElementClass, Class<T> targetElementClass) {
        return mapSet(source, sourceElementClass, null, targetElementClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T map(Object source, JavaBeanType<?> sourceType, T target, JavaBeanType<T> targetType) {
        return map(source, sourceType, JApi.noQualifier(), target, targetType, JApi.noQualifier());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T map(Object source, JavaBeanType<?> sourceType, AQualifier sourceQualifier, T target, JavaBeanType<T> targetType, AQualifier targetQualifier) {
        return (T) inner.map(source, sourceType, sourceQualifier, target, targetType, targetQualifier);
    }
}
