package com.ajjpj.amapper.collection;

import com.ajjpj.amapper.core.path.APath;
import com.ajjpj.amapper.core.path.APathSegment;

/**
 * @author arno
 */
public class ACollectionMappingTools {
    public static APath elementPath (APath path, Object elIdent) {
        return path.withChild(APathSegment.parameterized("elements", elIdent));
    }
}
