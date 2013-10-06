package com.ajjpj.amapper.classes;

import com.ajjpj.amapper.javabean.japi.DeferredProperty;

/**
 * @author arno
 */
public class DiffSourceChild {
    private long oid;
    private double sourceNum;
    private DiffSource sourceParent;

    public DiffSourceChild(long oid, double sourceNum) {
        this.oid = oid;
        this.sourceNum = sourceNum;
    }

    public Long getOid() {
        return oid;
    }

    public void setOid(Long oid) {
        this.oid = oid;
    }

    public double getSourceNum() {
        return sourceNum;
    }

    public void setSourceNum(double sourceNum) {
        this.sourceNum = sourceNum;
    }

    @DeferredProperty
    public DiffSource getSourceParent() {
        return sourceParent;
    }

    public void setSourceParent(DiffSource sourceParent) {
        this.sourceParent = sourceParent;
    }
}
