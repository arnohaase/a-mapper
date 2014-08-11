package com.ajjpj.amapper.util;

import com.ajjpj.abase.util.AUnchecker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * This is a thin wrapper around reflective calls, essentially dealing with error handling
 *
 * @author arno
 */
public class AMapperReflectionHelper {
    public static Object invoke (Method mtd, Object target, Object... args) throws Exception {
        try {
            return mtd.invoke (target, args);
        }
        catch (InvocationTargetException exc) {
            AUnchecker.throwUnchecked (exc);
            return null;
        }
    }
}
