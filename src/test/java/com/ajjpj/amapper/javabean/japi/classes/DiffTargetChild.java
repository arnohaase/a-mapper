package com.ajjpj.amapper.javabean.japi.classes;

/**
 * @author arno
 */
public class DiffTargetChild {
    private long oid;
    private int targetNum;

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
}
