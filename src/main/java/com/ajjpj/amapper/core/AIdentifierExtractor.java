package com.ajjpj.amapper.core;

import com.ajjpj.amapper.core.tpe.AQualifiedSourceAndTargetType;
import com.ajjpj.amapper.core.tpe.AQualifiedType;


/**
 * @author arno
 */
public interface AIdentifierExtractor {
    /**
     * Used to qualify the path segment in a collection mapping, or more generally to identify an object in the mapper's output. The mapper uses these
     *  identifiers when mapping collections to minimize changes to the target. They also qualify paths segments for collection elements, and application
     *  code may depend on them, especially when processing the result of a 'diff'. <p>
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
     * @param type the qualified type of the object for which the identifier is extracted. For some domains this is redundant, e.g. for Java Beans. In other domains this
     *              information is however difficult or impossible to extract from the object itself, e.g. if all objects are represented as maps. Just ignore this if you
     *              don't need this.
     *
     * @param targetType the qualified type in terms of which the identifier is to be generated. This is useful for the rare case that an object's identifier depends on the the
     *                    target domain - e.g. a business object may be represented by a synthetic primary key in the context of a database and by a business key in the context
     *                    of an external API. If this does not make sense to you, just ignore this parameter.
     */
    Object uniqueIdentifier (Object o, AQualifiedType type, AQualifiedType targetType);
}
