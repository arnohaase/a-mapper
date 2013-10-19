package com.ajjpj.amapper.core.compile;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author arno
 */
class ACodeBuilderForMappingDef {
    public static final String PACKAGE_NAME = "com.ajjpj.amapper.generated";

    private final Collection<AInjectedField> injected = new ArrayList<AInjectedField>();
    private final Collection<String> supports = new ArrayList<String>();
    private final Collection<String> methods = new ArrayList<String>();

    public final String simpleName = ACodeSnippet.uniqueIdentifier();
    public final String fqn = PACKAGE_NAME + "." + simpleName;

    private final String innerType;
    private final ACodeBuilder code = new ACodeBuilder(0);

    ACodeBuilderForMappingDef(String innerType) {
        this.innerType = innerType;
    }

    public void addInjectedFields(Collection<AInjectedField> injected) {
        this.injected.addAll(injected);
    }

    public void addSupports(Collection<String> supports) {
        this.supports.addAll(supports);
    }

    public void addMethod(String header, String body) {
        final ACodeBuilder methodBuilder = new ACodeBuilder(0);
        methodBuilder.appendLine(1, header);
        methodBuilder.append(0, body);
        methodBuilder.appendLine(1, "}");

        methods.add(methodBuilder.build());
    }

    public String build() {
        code.appendLine(0, "package " + PACKAGE_NAME + ";");
        code.appendLine();
        code.appendLine(0, "public class " + simpleName + " implements " + innerType + " {");
        appendFieldDefs();
        code.appendLine();
        appendConstructor();
        code.appendLine();
        appendSupports();

        appendDelegatedMethods();

        for(String method: methods) {
            code.appendLine();
            code.append(0, method);
        }

        code.appendLine(0, "}");


//        System.out.println("----------------------------------------");
//        System.out.println(code.build());
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");


        return code.build();
    }

    private void appendFieldDefs() {
        code.appendLine(1, "private final " + innerType + " inner;");
        for(AInjectedField aif: injected) {
            code.appendLine(1, "private final " + aif.getFieldType() + " " + aif.getFieldName() + ";");
        }
    }

    private void appendConstructor() {
        code.appendLine(1, "public " + simpleName + " (" + innerType + " inner");
        appendInjectedParameters();
        code.appendLine(3, ") {");
        code.appendLine(2, "this.inner = inner;");
        appendInjectedInit();
        code.appendLine(1, "}");
    }

    private void appendInjectedParameters() {
        for(AInjectedField aif: injected) {
            code.appendLine(3, ", " + aif.getFieldType() + " " + aif.getFieldName());
        }
    }
    private void appendInjectedInit() {
        for(AInjectedField aif: injected) {
            code.appendLine(2, "this." + aif.getFieldName() + " = " + aif.getFieldName() + ";");
        }
    }

    private void appendSupports() {
        for(String support: supports) {
            code.appendLine(1, support);
        }
    }

    private void appendDelegatedMethods() {
        code.appendLine(1, "public boolean canHandle(com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType types) throws Exception {");
        code.appendLine(2, "return inner.canHandle(types);");
        code.appendLine(1, "}");
        code.appendLine();
        code.appendLine(1, "public boolean isCacheable() {");
        code.appendLine(2, "return inner.isCacheable();");
        code.appendLine(1, "}");
    }
}
