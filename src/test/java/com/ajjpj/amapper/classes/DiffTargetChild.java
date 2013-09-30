package com.ajjpj.amapper.classes;

import com.ajjpj.amapper.javabean.japi.DeferredProperty;

/**
 * @author arno
 */
public class DiffTargetChild {
    private long oid;
    private int targetNum;
    private DiffTarget targetParent;

    public DiffTargetChild(long oid, int targetNum) {
        this.oid = oid;
        this.targetNum = targetNum;
    }

    public Long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public int getTargetNum() {
        return targetNum;
    }

    public void setTargetNum(int targetNum) {
        this.targetNum = targetNum;
    }

    @DeferredProperty
    public DiffTarget getTargetParent() {
        return targetParent;
    }

    public void setTargetParent(DiffTarget targetParent) {
        this.targetParent = targetParent;
    }
}
