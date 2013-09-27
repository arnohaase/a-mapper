package com.ajjpj.amapper.javabean2;

/**
 * @author arno
 */
public class SingleParamBeanType<T,P> extends JavaBeanType<T> {
    public final Class<P> param;

    public SingleParamBeanType(Class<T> cls, Class<P> param) {
        super(cls);
        this.param = JavaBeanTypes.normalized(param);
    }

    @Override public String getName() {
        return cls.getName() + "<" + param.getName() + ">";
    }

    public JavaBeanType<P> getParamType() {
        return JavaBeanTypes.create(param);
    }

    @Override
    public String toString() {
        return "SingleParamBeanType{" +
                "cls=" + cls.getName() +
                "param=" + param.getName() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SingleParamBeanType that = (SingleParamBeanType) o;

        if (!param.equals(that.param)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + param.hashCode();
        return result;
    }
}
