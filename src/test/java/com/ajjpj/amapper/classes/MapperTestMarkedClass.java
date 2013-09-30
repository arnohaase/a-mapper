package com.ajjpj.amapper.classes;


public class MapperTestMarkedClass implements MapperTestMarker {
    private String dummy;

    @Override
    public void setDummy(String dummy) {
        this.dummy = dummy;
    }

    public String getDummy() {
        return dummy;
    }
}
