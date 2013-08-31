package com.ajjpj.amapper.javabean.japi.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ClassA {
    private String firstName;
    private String lastName;
    private int numChildren;
    private Date birthday;

    private MapperTestEnum e;

    private List<String> listAsSet = new ArrayList<String>();
    
    private List<InnerClassA> phone = new ArrayList<InnerClassA>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public List<InnerClassA> getPhone() {
        return phone;
    }

    public void setPhone(List<InnerClassA> phone) {
        this.phone = phone;
    }

    public MapperTestEnum getE() {
        return e;
    }

    public void setE(MapperTestEnum e) {
        this.e = e;
    }
    
    public List<String> getListAsSet() {
        return listAsSet;
    }
    
    public void setListAsSet(List<String> listAsSet) {
        this.listAsSet = listAsSet;
    }
}
