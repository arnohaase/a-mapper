package com.ajjpj.amapper.core2;

import com.ajjpj.amapper.core2.tpe.AType;

/**
 * @author arno
 */
public interface AIdentifierExtractor {
    /**
     * Used to qualify the path segment in a collection mapping, or more generally to identify an object in the mapper's output.. The mapper itself
     *  does not rely on it being unique, but using code may - especially when creating a 'diff'. <p />
     * The following properties are often desirable for uniqueIdentifier implementations (pretty much the same criteria
     *  that apply to good 'business keys' in persistent storage):
     * <ul>
     * <li> stable:    Running the same code with the same data returns the same key every time (as opposed to e.g. System.identityHashCode)
     * <li> unique:    "Different" elements have different identifiers, i.e. from an application perspective - old and new versions of the "same" element might well have the same identifier
     * <li> selective: Several versions of the "same" element have the same identifier - e.g. correcting a typo in a person's name does not change that person's identifier
     * <li> serializable / human readable: is often a direct consequence of "stable"
     * </ul>
     */
    Object uniqueIdentifier(Object o, AType tpe);
}
