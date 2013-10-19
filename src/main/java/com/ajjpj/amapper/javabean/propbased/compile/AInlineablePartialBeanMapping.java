package com.ajjpj.amapper.javabean.propbased.compile;

import com.ajjpj.amapper.core.compile.ACodeSnippet;
import com.ajjpj.amapper.core.compile.ACompilationContext;

/**
 * @author arno
 */
public interface AInlineablePartialBeanMapping {
    ACodeSnippet javaCodeForMap(ACodeSnippet source, ACodeSnippet target, ACompilationContext compilationContext) throws Exception;
    ACodeSnippet javaCodeForDiff(ACodeSnippet sourceOld, ACodeSnippet sourceNew, ACompilationContext compilationContext);
}
