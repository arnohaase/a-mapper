package com.ajjpj.amapper.classes;

import java.util.List;
import java.util.Map;

/**
 * @author arno
 */
public class ForTypeTests<T> {
    public String withString() {return null;}
    public List<String> withStringList() {return null;}
    public Map<String,String> withMap() {return null;}

    public List<?> withWildcardList() {return null;}
    public T withGeneric() {return null;}
}
