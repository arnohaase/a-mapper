package com.ajjpj.amapper.core.compile;


import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;

/**
 * This interface represents a value mapping def that supports 'compilation', i.e. creating equivalent Java source code
 *  that can then be compiled into Java classes at initialization time. More specifically, it represents a value mapping
 *  def that can be <em>inlined</em>. <p>
 *
 * Inlining here means the following: Instead of calling the worker with type information, calling mapping defs are
 *  encouraged to instead insert / use / call a static bit of Java code that is provided through this interface.
 *  This typically works well for 'simple' value mapping defs that do not evaluate type or qualifier information at
 *  runtime.<p>
 *
 * The returned code snipped must be a valid Java expression. It must be null-safe if null is a possible input value.
 *  It must evaluate to the target value itself *without* wrapping it into an AOption.some().
 *
 * It is optional for AValueMappingDef implementations to implement this interface. If they do, it is their responsibility
 *  to return valid Java source code that is equivalent to what the 'regular' implementation does.<p>
 *
 * This interface intentionally does not extend AValueMappingDef. While usually implementations will implement both, there
 *  is no inherent coupling between the two.<p>
 *
 * Generated Java code may rely on variables 'worker' and 'context' with the types they have in AValueMappingDef methods
 *  being in scope.
 *
 * @author arno
 */
public interface AInlineableValueMappingDef {
    ACodeSnippet javaCodeForMap(ACodeSnippet sourceValue, AQualifiedSourceAndTargetType types);
    ACodeSnippet javaCodeForDiff(ACodeSnippet sourceOld, ACodeSnippet sourceNew, AQualifiedSourceAndTargetType types);
}
