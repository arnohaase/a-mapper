package com.ajjpj.amapper.classes.duplicateidentity;

import com.ajjpj.amapper.javabean.annotation.DeferredProperty;


/**
 * @author arno
 */
public class ChildA {
    private ParentA parent;

    @DeferredProperty
    public ParentA getParent () {
        return parent;
    }
    public void setParent (ParentA parent) {
        this.parent = parent;
    }
}
