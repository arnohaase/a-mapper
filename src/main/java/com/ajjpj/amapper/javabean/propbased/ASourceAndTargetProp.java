package com.ajjpj.amapper.javabean.propbased;

import com.ajjpj.amapper.core.AMapperDiffWorker;
import com.ajjpj.amapper.core.AMapperWorker;
import com.ajjpj.amapper.core.AValueMappingDef;
import com.ajjpj.amapper.core.compile.*;
import com.ajjpj.amapper.core.diff.ADiffBuilder;
import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.path.APathSegment;
import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.javabean.JavaBeanMappingHelper;
import com.ajjpj.amapper.javabean.propbased.accessors.APropertyAccessor;
import com.ajjpj.amapper.javabean.propbased.compile.AInlineablePartialBeanMapping;
import com.ajjpj.amapper.util.coll.AMap;
import com.ajjpj.amapper.util.coll.AOption;
import com.ajjpj.amapper.util.func.AFunction0;
import com.ajjpj.amapper.util.func.AVoidFunction1;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author arno
 */
public class ASourceAndTargetProp<S,T> implements APartialBeanMapping<S,T,JavaBeanMappingHelper>, AInlineablePartialBeanMapping {
    private final APropertyAccessor sourceProp;
    private final APropertyAccessor targetProp;

    private final AQualifiedSourceAndTargetType types;

    public ASourceAndTargetProp(APropertyAccessor sourceProp, APropertyAccessor targetProp) {
        if(! targetProp.isWritable()) {
            throw new IllegalArgumentException("target property is not writable: " + targetProp); //TODO weaken this condition - or how to handle this?!
        }

        this.sourceProp = sourceProp;
        this.targetProp = targetProp;

        this.types = new AQualifiedSourceAndTargetType (sourceProp.getType(), sourceProp.getSourceQualifier(), targetProp.getType(), targetProp.getTargetQualifier());
    }

    public ASourceAndTargetProp<T,S> reverse() {
        return new ASourceAndTargetProp<T, S>(targetProp, sourceProp);
    }

    @Override public String getSourceName() {
        return sourceProp.getName();
    }

    @Override public String getTargetName() {
        return targetProp.getName();
    }

    private APath childPath(APath path, boolean isSourceSide) {
        return path.withChild(APathSegment.simple(isSourceSide ? getSourceName() : getTargetName()));
    }

    @Override public String toString() {
        return "SourceAndTarget{" + sourceProp + " / " + targetProp + "}";
    }

    @Override public void doMap(S source, final T target, AMapperWorker<? extends JavaBeanMappingHelper> worker, AMap<String, Object> context, APath path) throws Exception {
        if(sourceProp.isDeferred()) {
            final AFunction0<Object,Exception> tp = new AFunction0<Object,Exception>() {
                @Override public Object apply() throws Exception {
                    return targetProp.get(target);
                }
            };

            worker.mapDeferred (childPath(path, true), sourceProp.get(source), tp, types, new AVoidFunction1<Object,Exception>() {
                @Override public void apply(Object o) throws Exception {
                    targetProp.set(target, o);
                }
            });
        }
        else {
            final Object oldTargetValue = targetProp.get(target);
            final AOption<Object> opt = worker.map(childPath(path, true), sourceProp.get(source), oldTargetValue, types, context);
            if(opt.isDefined() && opt.get() != oldTargetValue) {
                targetProp.set(target, opt.get());
            }
        }
    }

    @Override public void doDiff(ADiffBuilder diff, S sourceOld, S sourceNew, AMapperDiffWorker<? extends JavaBeanMappingHelper> worker, AMap<String, Object> contextOld, AMap<String, Object> contextNew, APath path, boolean isDerived) throws Exception {
        //TODO use 'default values' (e.g. based on an 'empty' target object) instead of this 'null' default --> add 'getDefaultValue()' to property accessor?
        final Object oldProp = sourceOld != null ? sourceProp.get(sourceOld) : null;
        final Object newProp = sourceNew != null ? sourceProp.get(sourceNew) : null; //TODO does sourceOld/New==null force 'isDerived'?

        if(sourceProp.isDeferred()) {
            worker.diffDeferred(childPath(path, false), oldProp, newProp, types, contextOld, contextNew, isDerived);
        }
        else {
            worker.diff (childPath (path, false), oldProp, newProp, types, contextOld, contextNew, isDerived);
        }
    }

    @Override public ACodeSnippet javaCodeForMap(ACodeSnippet source, ACodeSnippet target, ACompilationContext compilationContext) throws Exception {
        final ACodeBuilder code = new ACodeBuilder(2);

        final Collection<String> supports = new ArrayList<String>();
        final Collection<AInjectedField> injectedFields = new ArrayList<AInjectedField>();

        if(sourceProp.isDeferred()) {
            final String sourcePropName = ACodeSnippet.uniqueIdentifier();
            final String targetPropName = ACodeSnippet.uniqueIdentifier();
            injectedFields.add(new AInjectedField(sourcePropName, APropertyAccessor.class.getName(), sourceProp));
            injectedFields.add(new AInjectedField(targetPropName, APropertyAccessor.class.getName(), targetProp));

            final String typesVarName = ACodeSnippet.uniqueIdentifier();
            injectedFields.add(new AInjectedField(typesVarName, AQualifiedSourceAndTargetType.class.getName(), types));

            final String getterFunction0Name = ACodeSnippet.uniqueIdentifier();
            code.appendLine(1, "final " + AFunction0.class.getName() + " " + getterFunction0Name + " = new " + AFunction0.class.getName() + "() {");
            code.appendLine(2, "public Object apply() throws Exception {");
            code.appendLine(3, "return " + targetPropName + ".get(" + target.getCode() + ");");
            code.appendLine(2, "}");
            code.appendLine(1, "};");

            code.appendLine(1, "worker.mapDeferred (path.withChild(" + APathSegment.class.getName() + ".simple(\"" + getSourceName() + "\"))),");
            code.appendLine(3, sourcePropName + ".get(" + source.getCode() + "), " + getterFunction0Name + ", " + typesVarName + ",");
            code.appendLine(3, "new " + AVoidFunction1.class.getName() + "() {");
            code.appendLine(4, "public void apply(Object o) throws Exception {");
            code.appendLine(5, targetPropName + ".set(" + target.getCode() + ", o);");
            code.appendLine(4, "}");
            code.appendLine(3, "});");
        }
        else {
            final AOption<AValueMappingDef> vmOpt = compilationContext.tryGetValueMapping(types);
            // statically determine if the mapped values will be mapped as values or objects
            if(vmOpt.isDefined()) {
                final AValueMappingDef vm = vmOpt.get();

                if(vm instanceof AInlineableValueMappingDef) {
                    final AInlineableValueMappingDef ivm = (AInlineableValueMappingDef) vm;
                    final String getSource = mergedCodeSnippet(sourceProp.javaCodeForGet(source), supports, injectedFields);
                    final String mapped = mergedCodeSnippet(ivm.javaCodeForMap(new ACodeSnippet(getSource), types), supports, injectedFields);
                    final String setTarget = mergedCodeSnippet(targetProp.javaCodeForSet(target, new ACodeSnippet(mapped)), supports, injectedFields);
                    code.appendLine(1, setTarget + ";");
                }
                else {
                    final String injectedTypes = ACodeSnippet.uniqueIdentifier();
                    injectedFields.add(new AInjectedField(injectedTypes, AQualifiedSourceAndTargetType.class.getName(), types));

                    final String vmName = ACodeSnippet.uniqueIdentifier();
                    injectedFields.add(new AInjectedField(vmName, AValueMappingDef.class.getName(), vm));

                    final String getSource = mergedCodeSnippet(sourceProp.javaCodeForGet(source), supports, injectedFields);
                    final String invokeMap = vmName + ".map(" + getSource + ", " + injectedTypes + ", worker, context)";
                    final String cast = "((" + targetProp.getType().cls.getName() + ")" + invokeMap + ")";
                    final String setTarget = mergedCodeSnippet(targetProp.javaCodeForSet(target, new ACodeSnippet(cast)), supports, injectedFields);

                    code.appendLine(1, setTarget + ";");
                }
            }
            else {
                javaCodeForObjectMapping(source, target, code, supports, injectedFields);
            }
        }


        return new ACodeSnippet(code.build(), supports, injectedFields);
    }

    private void javaCodeForObjectMapping(ACodeSnippet source, ACodeSnippet target, ACodeBuilder code, Collection<String> supports, Collection<AInjectedField> injectedFields) throws Exception {
        final String injectedTypes = ACodeSnippet.uniqueIdentifier();
        injectedFields.add(new AInjectedField(injectedTypes, AQualifiedSourceAndTargetType.class.getName(), types));

        final String oldTargetValueName = ACodeSnippet.uniqueIdentifier();
        final String optName = ACodeSnippet.uniqueIdentifier();

        code.appendLine(1, "final Object " + oldTargetValueName + " = " + mergedCodeSnippet(targetProp.javaCodeForGet(target), supports, injectedFields) + ";");
        code.appendLine(1, "final " + AOption.class.getName() + " " + optName + " = worker.map(path.withChild(" + APathSegment.class.getName() + ".simple(\"" + getSourceName() + "\")), " +
                mergedCodeSnippet(sourceProp.javaCodeForGet(source), supports, injectedFields) +
                ", " + oldTargetValueName + ", " + injectedTypes + ", context);");
        code.appendLine(1, "if (" + optName + ".isDefined() && " + optName + ".get() != " + oldTargetValueName + ") {");
        code.appendLine(2, mergedCodeSnippet(targetProp.javaCodeForSet(target, new ACodeSnippet("(" + sourceProp.getType().cls.getName() + ")" + optName + ".get()")), supports, injectedFields) + ";");
        code.appendLine(1, "}");
    }

    private String mergedCodeSnippet(ACodeSnippet code, Collection<String> supports, Collection<AInjectedField> injectedFields) {
        supports.addAll(code.getSupports());
        injectedFields.addAll(code.getInjectedFields());
        return code.getCode();
    }

    @Override public ACodeSnippet javaCodeForDiff(ACodeSnippet sourceOld, ACodeSnippet sourceNew, ACompilationContext compilationContext) {
        return new ACodeSnippet(""); //TODO
    }
}

































