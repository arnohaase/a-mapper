package com.ajjpj.amapper.classes;


import java.util.ArrayList;
import java.util.List;

public class ClassCyclicParent {
    private ClassCyclicChild child;
    private List<ClassCyclicChild> childList = new ArrayList<ClassCyclicChild>();

    public ClassCyclicChild getChild() {
        return child;
    }

    public void setChild(ClassCyclicChild child) {
        this.child = child;
    }

    public List<ClassCyclicChild> getChildList() {
        return childList;
    }

    public void setChildList(List<ClassCyclicChild> childList) {
        this.childList = childList;
    }
}
