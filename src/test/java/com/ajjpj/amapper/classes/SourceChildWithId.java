package com.ajjpj.amapper.classes;


public class SourceChildWithId {
    private int sourceId;
    
    private String sourceAttrib1;
    private int sourceAttrib2;

    public SourceChildWithId() {
    }

    public SourceChildWithId(int id, String attrib1, int attrib2) {
        this.sourceId = id;
        this.sourceAttrib1 = attrib1;
        this.sourceAttrib2 = attrib2;
    }
    
    public int getSourceId() {
        return sourceId;
    }
    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }
    public String getSourceAttrib1() {
        return sourceAttrib1;
    }
    public void setSourceAttrib1(String sourceAttrib1) {
        this.sourceAttrib1 = sourceAttrib1;
    }
    public int getSourceAttrib2() {
        return sourceAttrib2;
    }
    public void setSourceAttrib2(int sourceAttrib2) {
        this.sourceAttrib2 = sourceAttrib2;
    }

    @Override
    public String toString() {
        return "SourceChild (" + sourceId + ")";
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
        SourceChildWithId other = (SourceChildWithId) obj;
        if (sourceId != other.sourceId)
            return false;
        return true;
    }
}
