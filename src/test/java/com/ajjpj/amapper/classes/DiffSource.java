package com.ajjpj.amapper.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arno
 */
public class DiffSource {
    private long oid;
    private String sourceName;
    private DiffSourceChild sourceChild;
    private List<DiffSourceChild> sourceChildren = new ArrayList<DiffSourceChild>();

    public DiffSource(long oid, String sourceName, DiffSourceChild sourceChild) {
        this.oid = oid;
        this.sourceName = sourceName;
        this.sourceChild = sourceChild;
    }

    public Long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public DiffSourceChild getSourceChild() {
        return sourceChild;
    }

    public void setSourceChild(DiffSourceChild sourceChild) {
        this.sourceChild = sourceChild;
    }

    public List<DiffSourceChild> getSourceChildren() {
        return sourceChildren;
    }

    public void setSourceChildren(List<DiffSourceChild> sourceChildren) {
        this.sourceChildren = sourceChildren;
    }
}
