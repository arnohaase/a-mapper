package com.ajjpj.amapper.classes;

/**
 * @author arno
 */
public class WithProperties {
    public PartOfPropPath inner = new PartOfPropPath();

    private String theString;

    public String getAbc() {
        return theString;
    }

    public void setAbc(String s) {
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
