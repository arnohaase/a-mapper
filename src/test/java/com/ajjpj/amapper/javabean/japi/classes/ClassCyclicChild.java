package com.ajjpj.amapper.javabean.japi.classes;


import com.ajjpj.amapper.javabean.japi.DeferredProperty;

public class ClassCyclicChild {
    private String name;
    private String s;
    
    private ClassCyclicParent parent;

    @DeferredProperty
    public ClassCyclicParent getParent() {
        return parent;
    }
    public void setParent(ClassCyclicParent parent) {
        this.parent = parent;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getReadOnly() {
        return s;
    }
    public void setWriteOnly(String s) {
        this.s = s;
    }
}
