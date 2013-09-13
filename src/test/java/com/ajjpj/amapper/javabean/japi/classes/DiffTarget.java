package com.ajjpj.amapper.javabean.japi.classes;

/**
 * @author arno
 */
public class DiffTarget {
    private long oid;
    private String targetName;
    private DiffTargetChild targetChild;

    public DiffTarget(long oid, String targetName, DiffTargetChild targetChild) {
        this.oid = oid;
        this.targetName = targetName;
        this.targetChild = targetChild;
    }

    public Long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public DiffTargetChild getTargetChild() {
        return targetChild;
    }

    public void setTargetChild(DiffTargetChild targetChild) {
        this.targetChild = targetChild;
    }
}
