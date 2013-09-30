package com.ajjpj.amapper.classes;


public class ClassWithContext implements TestCurrencyProvider {
    private String currency;
    private ClassRequiringContext requiringContext;

    @Override
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public ClassRequiringContext getRequiringContext() {
        return requiringContext;
    }

    public void setRequiringContext(ClassRequiringContext requiringContext) {
        this.requiringContext = requiringContext;
    }
}
