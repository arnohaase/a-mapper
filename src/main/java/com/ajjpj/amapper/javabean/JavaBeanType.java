package com.ajjpj.amapper.javabean;

import com.ajjpj.amapper.core.tpe.AType;

/**
 * @author arno
 */
public class JavaBeanType<T> implements AType {
    public final Class<T> cls;

    public JavaBeanType(Class<T> cls) {
        this.cls = JavaBeanTypes.normalized(cls);
    }

    @Override public String getName() {
        return cls.getName();
    }

    @Override
    public String toString() {
        return "JavaBeanType{" +
                "cls=" + cls +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaBeanType that = (JavaBeanType) o;

        if (!cls.equals(that.cls)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return cls.hashCode();
    }
}
