package com.ajjpj.amapper.classes.duplicateidentity;

import com.ajjpj.amapper.javabean.annotation.DeferredProperty;


/**
 * @author arno
 */
public class ChildB {
    private ParentB2 parent;
    private String name2;

    @DeferredProperty
    public ParentB2 getParent () {
        return parent;
    }
    public void setParent (ParentB2 parent) {
        this.parent = parent;
    }

    public String getName2 () {
        return name2;
    }
    public void setName2 (String name2) {
        this.name2 = name2;
    }
}
