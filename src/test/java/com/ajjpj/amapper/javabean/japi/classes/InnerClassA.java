package com.ajjpj.amapper.javabean.japi.classes;


public class InnerClassA {
    private String phone;
    private String other;

    public InnerClassA() {
    }

    public InnerClassA(String phone, String other) {
        this.phone = phone;
        this.other = other;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
