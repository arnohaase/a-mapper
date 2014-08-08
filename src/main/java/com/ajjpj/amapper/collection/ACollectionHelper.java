package com.ajjpj.amapper.collection;


import com.ajjpj.amapper.core.tpe.AQualifiedType;
import com.ajjpj.amapper.core.tpe.AType;

import java.util.Collection;

/**
 * Collection handling algorithms are abstracted from the concrete collection types. They operate on instances of java.util.Collection, using only the following methods:
 * <ul>
 *     <li>add(o)</li>
 *     <li>remove(o)</li>
 *     <li>iterator()</li>
 * </ul>
 *
 * This interface defines the required adaptor methods between arbitrary collection representations and java.util.Collection. The 'helpers' object registered with the mapper
 *  and passed to mapping defs must implement ACollectionHelper in order for collection mappings to be available. <p>
 *
 * The mutator methods of the returned j.u.Collection can either write through to a wrapped collection, or the returned collection can act as a builder, creating a new
 *  underlying collection when fromJuCollection is called.
 *
 * @author arno
 */
public interface ACollectionHelper {
//    /**
//     * creates an empty 'wrapped' collection of given (target) type
//     */
//    <T> Collection<T> createEmptyCollection(AQualifiedSourceAndTargetType types);
    AType elementType(AType tpe);

    <T> Collection<T> asJuCollection(Object coll, AQualifiedType tpe);
    Object fromJuCollection(Collection<?> coll, AQualifiedType tpe);

    <T> Collection<T> createEmptyCollection(AQualifiedType tpe) throws Exception;
}
