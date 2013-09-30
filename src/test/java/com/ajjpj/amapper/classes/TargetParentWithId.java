package com.ajjpj.amapper.classes;

import java.util.HashSet;
import java.util.Set;


public class TargetParentWithId {
    private int targetId;
    private String targetAttrib;
    
    private TargetChildWithId targetChild;
    private Set<TargetChildWithId> targetChildren = new HashSet<TargetChildWithId>();

    public TargetParentWithId() {
    }

    public TargetParentWithId(int targetId, String targetAttrib) {
        this.targetId = targetId;
        this.targetAttrib = targetAttrib;
    }
    
    public int getTargetId() {
        return targetId;
    }
    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }
    public String getTargetAttrib() {
        return targetAttrib;
    }
    public void setTargetAttrib(String targetAttrib) {
        this.targetAttrib = targetAttrib;
    }
    public TargetChildWithId getTargetChild() {
        return targetChild;
    }
    public void setTargetChild(TargetChildWithId targetChild) {
        this.targetChild = targetChild;
    }
    public Set<TargetChildWithId> getTargetChildren() {
        return targetChildren;
    }
    public void setTargetChildren(Set<TargetChildWithId> targetChildren) {
        this.targetChildren = targetChildren;
    }

    @Override
    public String toString() {
        return "TargetParent (" + targetId +")";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + targetId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TargetParentWithId other = (TargetParentWithId) obj;
        if (targetId != other.targetId)
            return false;
        return true;
    }
}
