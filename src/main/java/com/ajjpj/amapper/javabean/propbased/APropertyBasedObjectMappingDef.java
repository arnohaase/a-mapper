package com.ajjpj.amapper.javabean.propbased;

import com.ajjpj.afoundation.collection.immutable.AMap;
import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.compile.*;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean.JavaBeanTypes;
import com.ajjpj.amapper.javabean.mappingdef.AbstractJavaBeanObjectMappingDef;
import com.ajjpj.amapper.javabean.propbased.compile.AInlineablePartialBeanMapping;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author arno
 */
public class APropertyBasedObjectMappingDef<S,T,H extends JavaBeanMappingHelper> extends AbstractJavaBeanObjectMappingDef<S,T,H> implements ACompilableObjectMappingDef {
    private final Collection<? extends APartialBeanMapping<S,T,? super H>> parts;

    public APropertyBasedObjectMappingDef(Class<S> sourceClass, Class<T> targetClass, Collection<? extends APartialBeanMapping<S, T, ? super H>> parts) {
        super(sourceClass, targetClass);
        this.parts = parts;
    }

    @Override protected void doMap(S source, T target, AQualifiedSourceAndTargetType types, AMapperWorker<? extends H> worker, AMap<String, Object> context, APath path) throws Exception {
        for(APartialBeanMapping<S,T,? super H> part: parts) {
            part.doMap(source, target, worker, context, path);
        }
    }

    @Override public void diff(ADiffBuilder diff, S sourceOld, S sourceNew, AQualifiedSourceAndTargetType types, AMapperDiffWorker<? extends H> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
        for(APartialBeanMapping<S,T,? super H> part: parts) {
            part.doDiff(diff, sourceOld, sourceNew, worker, contextOld, contextNew, path, isDerived);
        }
    }

    @Override public ACodeSnippet getSourceTypeRepresentation(AQualifiedSourceAndTargetType types) {
        return new ACodeSnippet(sourceType.cls.getName());
    }

    @Override public ACodeSnippet getTargetTypeRepresentation(AQualifiedSourceAndTargetType types) {
        return new ACodeSnippet(targetType.cls.getName());
    }

    @Override public ACodeSnippet javaCodeForMap(ACodeSnippet sourceRaw, ACodeSnippet target, ACompilationContext compilationContext) throws Exception {
        final Collection<AInjectedField> injectedFields = new ArrayList<AInjectedField>();

        final String source = ACodeSnippet.uniqueIdentifier();

        final ACodeBuilder code = new ACodeBuilder(0);
        code.appendLine(2, "if (" + sourceRaw.getCode() + " == null) {");
        code.appendLine(3, "return null;");
        code.appendLine(2, "}");
        code.appendLine(2, "else {");
        code.appendLine(3, "final " + sourceType.cls.getName() + " " + source + " = (" + sourceType.cls.getName() + ")" + sourceRaw.getCode() + ";");
        code.appendLine(3, "final " + targetType.cls.getName() + " target = (" + targetType.cls.getName() + ")(" + target.getCode() + " != null ? " +
                        target.getCode() + " : ((com.ajjpj.amapper.javabean.JavaBeanMappingHelper) worker.getHelpers()).provideInstance(" +
                        source + ", " + target.getCode() + ", " +
                        JavaBeanTypes.class.getName() + ".create(" + sourceType.cls.getName() + ".class), " +
                        JavaBeanTypes.class.getName() + ".create(" + targetType.cls.getName() + ".class)));"
        );

        for(APartialBeanMapping pm: parts) {
            if(pm instanceof AInlineablePartialBeanMapping) {
                final ACodeSnippet pmSnippet = ((AInlineablePartialBeanMapping) pm).javaCodeForMap(new ACodeSnippet(source), new ACodeSnippet("target"), compilationContext);
                injectedFields.addAll (pmSnippet.getInjectedFields());
                code.appendLine(0, pmSnippet.getCode());
            }
            else {
                final String pmFieldName = ACodeSnippet.uniqueIdentifier();
                injectedFields.add(new AInjectedField(pmFieldName, APartialBeanMapping.class.getName(), pm));
                code.appendLine(3, pmFieldName + ".doMap(" + source + ", target, worker, context, path);");
            }
        }

        code.appendLine(3, "return target;");
        code.appendLine(2, "}");
        return new ACodeSnippet(code.toString(), injectedFields);
    }

    @Override public ACodeSnippet javaCodeForDiff(ACodeSnippet sourceOld, ACodeSnippet sourceNew, ACompilationContext compilationContext) throws Exception {
        final Collection<AInjectedField> injectedFields = new ArrayList<AInjectedField>();

        final ACodeBuilder code = new ACodeBuilder(0);

        final String sourceOldName = ACodeSnippet.uniqueIdentifier();
        final String sourceNewName = ACodeSnippet.uniqueIdentifier();

        code.appendLine (2, "final " + sourceType.cls.getName() + " " + sourceOldName + " = (" + sourceType.cls.getName() + ") " + sourceOld.getCode() + ";");
        code.appendLine (2, "final " + sourceType.cls.getName() + " " + sourceNewName + " = (" + sourceType.cls.getName() + ") " + sourceNew.getCode() + ";");

        for(APartialBeanMapping pm: parts) {
            if(pm instanceof AInlineablePartialBeanMapping) {
                final ACodeSnippet pmSnippet = ((AInlineablePartialBeanMapping) pm).javaCodeForDiff(new ACodeSnippet(sourceOldName), new ACodeSnippet(sourceNewName), compilationContext);
                injectedFields.addAll (pmSnippet.getInjectedFields());
                code.append(0, pmSnippet.getCode());
            }
            else {
                final String pmFieldName = ACodeSnippet.uniqueIdentifier();
                injectedFields.add(new AInjectedField(pmFieldName, APartialBeanMapping.class.getName(), pm));
                code.appendLine(2, pmFieldName + ".doDiff(diff, " + sourceOld.getCode() + ", " + sourceNew.getCode() + ", worker, contextOld, contextNew, path, isDerived);");
            }
        }

        return new ACodeSnippet(code.toString(), injectedFields);
    }
}
