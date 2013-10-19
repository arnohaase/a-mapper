package com.ajjpj.amapper.core.compile;

/**
 * @author arno
 */
public class ACodeBuilder {
    private final int inheritedIndent;
    private final StringBuilder code = new StringBuilder();

    public ACodeBuilder(int inheritedIndent) {
        this.inheritedIndent = inheritedIndent;
    }

    public String indent(int level) {
        return "                                                                            ".substring(0, 4*(level + inheritedIndent));
    }

    public void append(int indent, String line) {
        code.append(indent(indent));
        code.append(line);
    }
    public void appendLine(int indent, String line) {
        append(indent, line);
        appendLine();
    }
    public void appendLine() {
        code.append("\n");
    }

    public String build() {
        return code.toString();
    }

    @Override public String toString() {
        return build();
    }
}
