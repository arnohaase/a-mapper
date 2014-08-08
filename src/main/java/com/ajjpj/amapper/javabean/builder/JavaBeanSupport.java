package com.ajjpj.amapper.javabean.builder;

import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.amapper.core.tpe.AQualifier;
import com.ajjpj.amapper.javabean.JavaBeanType;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.builder.qualifier.AQualifierExtractor;
import com.ajjpj.amapper.javabean.propbased.accessors.AMethodBasedPropertyAccessor;
import com.ajjpj.amapper.javabean.propbased.accessors.APropertyAccessor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * This is a collection of generic helper methods for working with Java Beans
 *
 * @author arno
 */
public class JavaBeanSupport {
    //TODO public or other visibilities as well? --> recursively up the inheritance, getDeclaredMethods(), setAccessible(true)

    public static final String[] GETTER_PREFIXES = new String[] {"get", "is", "has"};

    private final AIsDeferredStrategy deferredStrategy;
    private final AQualifierExtractor qualifierExtractor;

    public JavaBeanSupport(AIsDeferredStrategy deferredStrategy, AQualifierExtractor qualifierExtractor) {
        this.deferredStrategy = deferredStrategy;
        this.qualifierExtractor = qualifierExtractor;
    }

    public AOption<APropertyAccessor> getBeanProperty(Class<?> cls, String name) throws Exception {
        final AOption<AccessorDetails> optGetter = getGetter(cls, name);
        if(optGetter.isEmpty()) {
            return AOption.none();
        }

        final AccessorDetails getter = optGetter.get();
        final AOption<AccessorDetails> optSetter = getSetterFor(name, getter.method);

        if(optSetter.isDefined()) {
            final AccessorDetails setter = optSetter.get();
            return AOption.some((APropertyAccessor) new AMethodBasedPropertyAccessor(name, getter.method, setter.method, getter.isDeferred || setter.isDeferred, getter.tpe, getter.qualifier, setter.qualifier));
        }
        else {
            return AOption.some((APropertyAccessor) new AMethodBasedPropertyAccessor(name, getter.method, null, getter.isDeferred, getter.tpe, getter.qualifier, AQualifier.NO_QUALIFIER));
        }
    }

    public Map<String, APropertyAccessor> getAllProperties(Class<?> cls) throws Exception {
        final Map<String, APropertyAccessor> result = new HashMap<String, APropertyAccessor>();

        for(Method mtd: cls.getMethods()) {
            if(mtd.getParameterTypes().length > 0) {
                continue;
            }
            if(mtd.getDeclaringClass() == Object.class) {
                continue;
            }

            for(String prefix: GETTER_PREFIXES) {
                if(mtd.getName().startsWith(prefix)) {
                    final AOption<APropertyAccessor> optProp = getBeanProperty(cls, methodNameSuffixToPropName(mtd.getName().substring(prefix.length())));
                    if(optProp.isDefined()) {
                        result.put(optProp.get().getName(), optProp.get());
                    }
                }
            }
        }
        return result;
    }

    private static String propNameToMethodNameSuffix(String propName) {
        return Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
    }

    private static String methodNameSuffixToPropName(String methodNameSuffix) {
        if(methodNameSuffix.length() > 1 && Character.isUpperCase(methodNameSuffix.charAt(1))) {
            return methodNameSuffix;
        }
        return Character.toLowerCase(methodNameSuffix.charAt(0)) + methodNameSuffix.substring(1);
    }

    private Set<AccessorDetails> getGetters(Class<?> cls, String name) throws Exception {
        final String methodNameSuffix = propNameToMethodNameSuffix(name);
        final Set<AccessorDetails> result = new HashSet<AccessorDetails>();

        for(String prefix: GETTER_PREFIXES) {
            try {
                final Method getter = cls.getMethod(prefix + methodNameSuffix);
                result.add(new AccessorDetails(getter, name, JavaBeanTypes.create(getter.getGenericReturnType()), qualifierExtractor.extract(getter), deferredStrategy.isDeferred(getter)));
            } catch (Exception e) { //
            }
        }

        return result;
    }

    public AOption<AccessorDetails> getGetter(Class<?> cls, String name) throws Exception {
        final Set<AccessorDetails> all = getGetters(cls, name);
        switch(all.size()) {
            case 0: return AOption.none();
            case 1: return AOption.some(all.iterator().next());
            default: return AOption.none(); //TODO log non-unique getter
        }
    }

    public AOption<AccessorDetails> getSetterFor(String name, Method getter) throws Exception {
        // passing in the getter instead of the JavaBeanType takes care of the whole primitive / boxed dichotomy
        try {
            final Method setter = getter.getDeclaringClass().getMethod("set" + propNameToMethodNameSuffix(name), getter.getReturnType());
            return AOption.some(new AccessorDetails(setter, name, JavaBeanTypes.create(getter.getGenericReturnType()), qualifierExtractor.extract(setter), deferredStrategy.isDeferred(setter)));
        }
        catch(Exception exc) { //
            return AOption.none();
        }
    }

    public static class AccessorDetails {
        public final Method method;
        public final String propName;
        public final JavaBeanType<?> tpe;
        public final AQualifier qualifier;
        public final boolean isDeferred;

        public AccessorDetails(Method method, String propName, JavaBeanType<?> tpe, AQualifier qualifier, boolean deferred) {
            this.method = method;
            this.propName = propName;
            this.tpe = tpe;
            this.qualifier = qualifier;
            isDeferred = deferred;
        }

        public boolean isGetter() {
            return method.getParameterTypes().length == 0;
        }
    }
}
