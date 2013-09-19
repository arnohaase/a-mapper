package com.ajjpj.amapper.examples.gettingstarted;

/**
 * @author arno
 */
public class PersonTO {
    private String firstName;
    private String surName;
    private String fullName;
    private short numChildren;

    private AddressTO address;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public short getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(short numChildren) {
        this.numChildren = numChildren;
    }

    public AddressTO getAddress() {
        return address;
    }

    public void setAddress(AddressTO address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PersonTO{" +
                "firstName='" + firstName + '\'' +
                ", surName='" + surName + '\'' +
                ", numChildren=" + numChildren +
                ", address=" + address +
                '}';
    }
}
