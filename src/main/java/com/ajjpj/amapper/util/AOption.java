package com.ajjpj.amapper.util;

/**
 * @author arno
 */
public abstract class AOption<T> {
    public static <T> AOption<T> some(T el) {
        return new ASome<T>(el);
    }

    @SuppressWarnings("unchecked")
    public static <T> AOption<T> none() {
        return  (AOption<T>) ANone.INSTANCE;
    }

    public static <T> AOption<T> fromNullable(T nullable) {
        return nullable != null ? some(nullable) : AOption.<T>none();
    }

    public abstract boolean isDefined();

    public abstract T get();
    public T getOrElse(T el) {
        return isDefined() ? get() : el;
    }
}

class ASome<T> extends AOption<T> {
    private final T el;

    ASome(T el) {
        this.el = el;
    }

    @Override public T get() {
        return el;
    }

    @Override public boolean isDefined() {
        return true;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASome aSome = (ASome) o;

        if (el != null ? !el.equals(aSome.el) : aSome.el != null) return false;

        return true;
    }

    @Override public int hashCode() {
        return el != null ? el.hashCode() : 0;
    }
}

class ANone extends AOption<Object> {
    public static final ANone INSTANCE = new ANone();

    @Override public Object get() {
        throw new IllegalStateException("no value for ANone");
    }

    @Override public boolean isDefined() {
        return false;
    }
}