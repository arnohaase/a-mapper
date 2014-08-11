package com.ajjpj.amapper.classes.duplicateidentity;

import com.ajjpj.amapper.javabean.annotation.DeferredProperty;


/**
 * @author arno
 */
public class ChildB {
    private ParentB2 parent;

    @DeferredProperty
    public ParentB2 getParent () {
        return parent;
    }
    public void setParent (ParentB2 parent) {
        this.parent = parent;
    }
}
