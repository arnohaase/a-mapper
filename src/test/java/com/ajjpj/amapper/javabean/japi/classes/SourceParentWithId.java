package com.ajjpj.amapper.javabean.japi.classes;

import java.util.HashSet;
import java.util.Set;


public class SourceParentWithId {
    private int sourceId;
    private String sourceAttrib;
    
    private SourceChildWithId sourceChild;
    private Set<SourceChildWithId> sourceChildren = new HashSet<SourceChildWithId>();

    public SourceParentWithId() {
    }

    public SourceParentWithId(int sourceId, String sourceAttrib) {
        this.sourceId = sourceId;
        this.sourceAttrib = sourceAttrib;
    }
    
    public int getSourceId() {
        return sourceId;
    }
    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }
    public String getSourceAttrib() {
        return sourceAttrib;
    }
    public void setSourceAttrib(String sourceAttrib) {
        this.sourceAttrib = sourceAttrib;
    }
    public SourceChildWithId getSourceChild() {
        return sourceChild;
    }
    public void setSourceChild(SourceChildWithId sourceChild) {
        this.sourceChild = sourceChild;
    }
    public Set<SourceChildWithId> getSourceChildren() {
        return sourceChildren;
    }
    public void setSourceChildren(Set<SourceChildWithId> sourceChildren) {
        this.sourceChildren = sourceChildren;
    }

    @Override
    public String toString() {
        return "SourceParent(" + sourceId + ")";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + sourceId;
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
        SourceParentWithId other = (SourceParentWithId) obj;
        if (sourceId != other.sourceId)
            return false;
        return true;
    }
}
