package com.ajjpj.amapper.classes.duplicateidentity;

import com.ajjpj.amapper.javabean.annotation.DeferredProperty;


/**
 * @author arno
 */
public class ChildA {
    private ParentA parent;
    private String name;

    @DeferredProperty
    public ParentA getParent () {
        return parent;
    }
    public void setParent (ParentA parent) {
        this.parent = parent;
    }

    public String getName () {
        return name;
    }
    public void setName (String name) {
        this.name = name;
    }
}
