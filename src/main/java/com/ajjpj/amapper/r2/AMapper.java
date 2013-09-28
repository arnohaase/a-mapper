package com.ajjpj.amapper.r2;

import com.ajjpj.amapper.core2.diff.ADiff;
import com.ajjpj.amapper.core2.tpe.AQualifier;
import com.ajjpj.amapper.core2.tpe.AType;
import com.ajjpj.amapper.util.coll.AOption;


/**
 * @author arno
 */
public interface AMapper {
    AOption<Object> map(Object source, AType sourceType, AQualifier sourceQualifier, Object target, AType targetType, AQualifier targetQualifier) throws Exception;
    ADiff diff(Object sourceOld, Object sourceNew, AType sourceType, AQualifier sourceQualifier, AType targetType, AQualifier targetQualifier) throws Exception;
}
