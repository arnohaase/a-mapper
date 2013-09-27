package com.ajjpj.amapper.javabean2;

import com.ajjpj.amapper.collection2.ACollectionHelper;

/**
 * @author arno
 */
public interface JavaBeanMappingHelper extends ACollectionHelper {
    /**
     * All implicit object creation done by the mapper itself goes through this method. Implementations may use reflection
     *  to call the class' no-args constructor (which is the default behavior), but more sophisticated implementations
     *  could e.g. lookup JPA entities by their primary key, causing the mapper to merge data into persistent data
     *  structures.
     */
    Object createInstance(JavaBeanType<?> tpe, JavaBeanType<?> forSourceType) throws Exception;
}
