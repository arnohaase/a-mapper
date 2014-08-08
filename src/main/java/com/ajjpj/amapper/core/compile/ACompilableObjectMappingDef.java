package com.ajjpj.amapper.core.compile;

import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;


/**
 * This interface marks an object mapping def that is compilable, i.e. that can and should be replaced by the result
 *  of compiling Java code provided through this interface at initialization time. An object mapping def for a Java
 *  bean is a good candidate for that behavior: Compilation allows 'inlining' of getter and setter calls instead of
 *  using reflection at runtime.<p>
 *
 * The Java code is generated for every combination of source and target types and qualifiers. That can lead to
 *  significant code duplication, especially if a mapping is used with a wide variety of qualifier values, in which
 *  case the object mapping def may or may not be a good candidate for being 'compilable'. <p>
 *
 * The code snippets provided through this interface must contain the <em>body</em> of a Java method that does the
 *  actual object mapping. Returning values must be through explicit 'return' statements. The method bodies must do the
 *  equivalent of 'map' and 'diff', respectively. The method header and enclosing braces must not be part of the returned
 *  code snippets, they will be added by the AMapper compiler.<p>
 *
 * For <code>map()</code>, the following variables are in scope: 'types', 'worker', 'context' and 'path'. Source and
 *  target value are passed in as code snippets that contain Java expressions whose static type is what is provided
 *  by 'getSourceTypeRepresentation' and 'getTargetTypeRepresentation'.<p>
 *
 * Access to other mapping defs is through an instance of ACompilationContext that is passed to the generator methods.
 *
 * @author arno
 */
public interface ACompilableObjectMappingDef {
    ACodeSnippet getSourceTypeRepresentation(AQualifiedSourceAndTargetType types);
    ACodeSnippet getTargetTypeRepresentation(AQualifiedSourceAndTargetType types);

    ACodeSnippet javaCodeForMap(ACodeSnippet source, ACodeSnippet target, ACompilationContext compilationContext) throws Exception;
    ACodeSnippet javaCodeForDiff(ACodeSnippet sourceOld, ACodeSnippet sourceNew, ACompilationContext compilationContext) throws Exception;
}


