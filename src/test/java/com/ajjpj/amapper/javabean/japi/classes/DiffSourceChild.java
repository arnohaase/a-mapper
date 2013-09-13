package com.ajjpj.amapper.javabean.japi.classes;

/**
 * @author arno
 */
public class DiffSourceChild {
    private long oid;
    private double sourceNum;

    public DiffSourceChild(long oid, double sourceNum) {
        this.oid = oid;
        this.sourceNum = sourceNum;
    }

    public Long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public double getSourceNum() {
        return sourceNum;
    }

    public void setSourceNum(double sourceNum) {
        this.sourceNum = sourceNum;
    }
}
