package com.ajjpj.amapper.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arno
 */
public class DiffTarget {
    private long oid;
    private String targetName;
    private DiffTargetChild targetChild;
    private int derivedTargetNum;
    private List<DiffTargetChild> targetChildren = new ArrayList<DiffTargetChild>();

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

    public int getDerivedTargetNum() {
        return derivedTargetNum;
    }

    public void setDerivedTargetNum(int derivedTargetNum) {
        this.derivedTargetNum = derivedTargetNum;
    }

    public List<DiffTargetChild> getTargetChildren() {
        return targetChildren;
    }

    public void setTargetChildren(List<DiffTargetChild> targetChildren) {
        this.targetChildren = targetChildren;
    }
}
