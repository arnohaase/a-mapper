package com.ajjpj.amapper.javabean;

import com.ajjpj.amapper.collection.ACollectionHelper;

/**
 * @author arno
 */
public interface JavaBeanMappingHelper extends ACollectionHelper {
    /**
     * All implicit object creation done by the mapper itself goes through this method. Implementations may use reflection
     *  to call the class' no-args constructor (which is the default behavior), but more sophisticated implementations
     *  could e.g. lookup JPA entities by their primary key, causing the mapper to merge data into persistent data
     *  structures.<p>
     *
     * NB: This method is called even if a target instance already exists.
     */
    Object provideInstance(Object source, Object target, JavaBeanType<?> sourceType, JavaBeanType<?> targetType) throws Exception;
}
