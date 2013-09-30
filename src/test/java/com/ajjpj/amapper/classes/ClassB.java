package com.ajjpj.amapper.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ClassB {
    private String firstName;
    private String lastName;
    private long numChildren;
    private Date birthday;

    private MapperTestEnum e;

    private List<InnerClassB> phone = new ArrayList<InnerClassB>();

    private List<String> listAsSet = new ArrayList<String>();
    
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

    public long getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(long numChildren) {
        this.numChildren = numChildren;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public List<InnerClassB> getPhone() {
        return phone;
    }

    public void setPhone(List<InnerClassB> phone) {
        this.phone = phone;
    }

    public MapperTestEnum getE() {
        return e;
    }

    public void setE(MapperTestEnum e) {
        this.e = e;
    }

//    TODO @OmdListAsSet
    public List<String> getListAsSet() {
        return listAsSet;
    }
    
    public void setListAsSet(List<String> listAsSet) {
        this.listAsSet = listAsSet;
    }
}
