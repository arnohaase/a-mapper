package com.ajjpj.amapper.classes;


public class TargetChildWithId {
    private int targetId;
    
    private String targetAttrib1;
    private int targetAttrib2;

    public TargetChildWithId() {
    }

    public TargetChildWithId(int id, String attrib1, int attrib2) {
        this.targetId = id;
        this.targetAttrib1 = attrib1;
        this.targetAttrib2 = attrib2;
    }
    
    public int getTargetId() {
        return targetId;
    }
    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }
    public String getTargetAttrib1() {
        return targetAttrib1;
    }
    public void setTargetAttrib1(String targetAttrib1) {
        this.targetAttrib1 = targetAttrib1;
    }
    public int getTargetAttrib2() {
        return targetAttrib2;
    }
    public void setTargetAttrib2(int targetAttrib2) {
        this.targetAttrib2 = targetAttrib2;
    }

    @Override
    public String toString() {
        return "TargetChild (" + targetId + ")";
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
        TargetChildWithId other = (TargetChildWithId) obj;
        if (targetId != other.targetId)
            return false;
        return true;
    }
}
