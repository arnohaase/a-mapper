package com.ajjpj.amapper.collection2;

import com.ajjpj.amapper.core2.path.APath;
import com.ajjpj.amapper.core2.path.APathSegment;

/**
 * @author arno
 */
public class ACollectionMappingTools {
    public static APath elementPath (APath path, Object elIdent) {
        return path.withChild(APathSegment.parameterized("elements", elIdent));
    }
}
