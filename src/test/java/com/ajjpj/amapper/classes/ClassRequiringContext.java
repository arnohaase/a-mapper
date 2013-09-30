package com.ajjpj.amapper.classes;



public class ClassRequiringContext {
    private double amount;
    private PriceClass price;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PriceClass getPrice() {
        return price;
    }

    public void setPrice(PriceClass price) {
        this.price = price;
    }
}
