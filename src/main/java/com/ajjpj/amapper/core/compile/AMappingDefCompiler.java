package com.ajjpj.amapper.core.compile;

import com.ajjpj.afoundation.collection.immutable.AMap;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.AObjectMappingDef;
import com.ajjpj.amapper.core.AValueMappingDef;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.exclog.AMapperLogger;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import org.codehaus.janino.SimpleCompiler;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author arno
 */
public class AMappingDefCompiler {
    public static ACodeSnippet OBJECT_MAPPING_SOURCE_SNIPPET = new ACodeSnippet("source");
    public static ACodeSnippet OBJECT_MAPPING_TARGET_SNIPPET = new ACodeSnippet("targetRaw");

    public static ACodeSnippet OBJECT_MAPPING_SOURCE_OLD_SNIPPET = new ACodeSnippet("sourceOld");
    public static ACodeSnippet OBJECT_MAPPING_SOURCE_NEW_SNIPPET = new ACodeSnippet("sourceNew");

    private final AMapperLogger logger;
    private final ACompilationContext compCtx;

    private final Collection<AObjectMappingDef> compiledObjectMappingDefs = new ArrayList<AObjectMappingDef>();

    public AMappingDefCompiler(AMapperLogger logger, Collection<? extends AObjectMappingDef> objectMappingDefs, Collection<? extends AValueMappingDef> valueMappingDefs) throws Exception {
        this.logger = logger;
        this.compCtx = new ACompilationContextImpl(valueMappingDefs);

        for(AObjectMappingDef om: objectMappingDefs) {
            processObjectMappingDef(om, compCtx);
        }
    }

    private void processObjectMappingDef(AObjectMappingDef omRaw, ACompilationContext compCtx) throws Exception {
        if(omRaw instanceof ACompilableObjectMappingDef) {
            final ACompilableObjectMappingDef om = (ACompilableObjectMappingDef) omRaw;
            compiledObjectMappingDefs.add(compileObjectMappingDef(om));
        }
        else {
            compiledObjectMappingDefs.add(omRaw);
        }
    }

    public Collection<AObjectMappingDef> getCompiledObjectMappingDefs() {
        return compiledObjectMappingDefs;
    }

    AObjectMappingDef compileObjectMappingDef(ACompilableObjectMappingDef orig) throws Exception {
        final SimpleCompiler compiler = new SimpleCompiler();
        compiler.setDebuggingInformation(true, true, true);
        final ACodeBuilderForMappingDef builder = new ACodeBuilderForMappingDef(AObjectMappingDef.class.getName(), logger);

        final ACodeSnippet codeForMap  = orig.javaCodeForMap(AMappingDefCompiler.OBJECT_MAPPING_SOURCE_SNIPPET, AMappingDefCompiler.OBJECT_MAPPING_TARGET_SNIPPET, compCtx);
        final ACodeSnippet codeForDiff = orig.javaCodeForDiff(AMappingDefCompiler.OBJECT_MAPPING_SOURCE_OLD_SNIPPET, AMappingDefCompiler.OBJECT_MAPPING_SOURCE_NEW_SNIPPET, compCtx);

        builder.addInjectedFields(codeForMap. getInjectedFields());
        builder.addInjectedFields(codeForDiff.getInjectedFields());

        // only raw types in the signatures because neither Janino nor Javassist support generics

        builder.addMethod("public Object map(Object source, Object targetRaw, " +
                AQualifiedSourceAndTargetType.class.getName() + " types, " +
                AMapperWorker.class.getName() + " worker, " +
                AMap.class.getName() + " context, " +
                APath.class.getName() + " path) throws Exception {",
                codeForMap.getCode());

        builder.addMethod("public void diff(" +
                ADiffBuilder.class.getName() + " diff, Object sourceOld, Object sourceNew, " +
                AQualifiedSourceAndTargetType.class.getName() + " types, " +
                AMapperDiffWorker.class.getName() + " worker, " +
                AMap.class.getName() + " contextOld, " +
                AMap.class.getName() + " contextNew, " +
                APath.class.getName() + " path, boolean isDerived) throws Exception {",
                codeForDiff.getCode());

        compiler.cook(builder.build());
        final Class omCls = compiler.getClassLoader().loadClass(builder.fqn);

        // this assumes only one constructor. Seems a somewhat brittle, but no reason comes to mind why there should
        //  every be more than one
        final Constructor ctor = omCls.getConstructors()[0];

        final List<Object> ctorArgs = new ArrayList<Object>();
        ctorArgs.add(orig);
        for(AInjectedField aif: codeForMap.getInjectedFields()) {
            ctorArgs.add(aif.getFieldValue());
        }
        for(AInjectedField aif: codeForDiff.getInjectedFields()) {
            ctorArgs.add(aif.getFieldValue());
        }

        return (AObjectMappingDef) ctor.newInstance(ctorArgs.toArray());
    }
}




