package com.ajjpj.amapper.core2;

import com.ajjpj.amapper.core2.tpe.AQualifiedSourceAndTargetType;

/**
 * @author arno
 */
public interface AIdentifierExtractor {
    /**
     * Used to qualify the path segment in a collection mapping, or more generally to identify an object in the mapper's output. The mapper uses these
     *  identifiers when mapping collections to minimize changes to the target. They also qualify paths segments for collection elements, and application
     *  code may depend on them, especially when processing the result of a 'diff'. <p />
     *
     * The following properties are often desirable for uniqueIdentifier implementations (pretty much the same criteria
     *  that apply to good 'business keys' in persistent storage):
     * <ul>
     * <li> stable:    Running the same code with the same data returns the same key every time (as opposed to e.g. System.identityHashCode). For partially initialized or
     *                 uninitialized objects, this criterion can often be skipped. Make sure that even for those objects, uniqueness is maintained!
     * <li> unique:    "Different" elements have different identifiers, i.e. from an application perspective - old and new versions of the "same" element might well have the same identifier
     * <li> selective: Several versions of the "same" element have the same identifier - e.g. correcting a typo in a person's name does not change that person's identifier
     * <li> cheap:     Identifiers are repeatedly calculated, and implementations assume that these calculations are not exensive.
     * <li> serializable / human readable: is often a direct consequence of "stable"
     * </ul>
     *
     * @param types contains the qualified type of the object that is passed in, as well as the type in terms of which it is viewed. <p />
     *              This information be redundant for some domains, e.g. for Java Beans. In other domains, an object's 'AType' may not be easily retrieved
     *              from the object, e.g. because all objects are represented by Map instances. So use what you need, and feel free to ignore
     *              the rest.
     */
    Object uniqueIdentifier(Object o, AQualifiedSourceAndTargetType types);
}
