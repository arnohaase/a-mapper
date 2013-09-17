package com.ajjpj.amapper.javabean.japi.classes;

/**
 * @author arno
 */
public class WithProperties {
    public PartOfPropPath inner = new PartOfPropPath();

    private String theString;

    String getAbc() {
        return theString;
    }

    void setAbc(String s) {
        theString = s;
    }

    public String getXyz() {
        return theString;
    }

    public void setXyz(String s) {
        theString = s;
    }

    public PartOfPropPath getOther() {
        return inner;
    }

    public String getTheString() {
        return theString;
    }
    public void setTheString(String theString) {
        this.theString = theString;
    }

    public String getReadOnly() {
        return theString;
    }

    public void setWriteOnly(String s) {
        theString = s;
    }
}
