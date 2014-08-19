package com.ajjpj.amapper.javabean;

/**
 * @author arno
 */
public class SingleParamBeanType<T,P> extends JavaBeanType<T> {
    public final Class<P> paramCls;

    public SingleParamBeanType(Class<T> cls, Class<P> param) {
        super(cls);
        this.paramCls = param;
    }

    @Override public String getName() {
        return cls.getName() + "<" + paramCls.getName() + ">";
    }

    public JavaBeanType<P> getParamType() {
        return JavaBeanTypes.create(paramCls);
    }

    @Override
    public String toString() {
        return "SingleParamBeanType{" +
                cls.getName() + "<" +
                paramCls.getName() + ">}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SingleParamBeanType that = (SingleParamBeanType) o;

        if (!paramCls.equals(that.paramCls)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + paramCls.hashCode();
        return result;
    }
}
