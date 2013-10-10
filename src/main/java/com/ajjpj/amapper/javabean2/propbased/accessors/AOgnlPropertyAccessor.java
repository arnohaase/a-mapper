package com.ajjpj.amapper.javabean2.propbased.accessors;

import com.ajjpj.amapper.core2.tpe.AQualifier;
import com.ajjpj.amapper.javabean2.JavaBeanType;
import ognl.Ognl;
import ognl.OgnlException;

/**
 * @author arno
 */
public class AOgnlPropertyAccessor implements APropertyAccessor {
    private final String name;
    private final Object parsedExpr;
    private final boolean isDeferred;
    private final boolean isWritable;
    private final JavaBeanType<?> tpe;
    private final AQualifier sourceQualifier;
    private final AQualifier targetQualifier;

    public AOgnlPropertyAccessor(String name, String expr, Class<?> parentClass, boolean deferred, JavaBeanType<?> tpe, AQualifier sourceQualifier, AQualifier targetQualifier) throws OgnlException {
        this.name = name;
        this.parsedExpr = Ognl.parseExpression(expr);
        isDeferred = deferred;
        isWritable = guessWritable(parentClass, parsedExpr);
        this.tpe = tpe;
        this.sourceQualifier = sourceQualifier;
        this.targetQualifier = targetQualifier;
    }

    private static boolean guessWritable(Class<?> parentClass, Object parsedExpr) {
        try {
            final Object o = parentClass.newInstance();
            // get the value and set it back - if that works, the property is apparently writable
            Ognl.setValue(parsedExpr, o, Ognl.getValue(parsedExpr, o));
            return true;
        }
        catch(Exception exc) {
            return false;
        }
    }

    @Override public String getName() {
        return name;
    }

    @Override public JavaBeanType<?> getType() {
        return tpe;
    }

    @Override public AQualifier getSourceQualifier() {
        return sourceQualifier;
    }

    @Override public AQualifier getTargetQualifier() {
        return targetQualifier;
    }

    @Override public boolean isDeferred() {
        return isDeferred;
    }

    @Override public boolean isWritable() {
        return isWritable;
    }

    @Override public Object get(Object o) throws Exception {
        return Ognl.getValue(parsedExpr, o);
    }

    @Override public void set(Object o, Object newValue) throws Exception {
        Ognl.setValue(parsedExpr, o, newValue);
    }

    @Override
    public String toString() {
        return "AOgnlPropertyAccessor{" + name + "}";
    }
}
