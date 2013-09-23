package com.ajjpj.amapper.examples.doubleToBigDecimalQualifier;

/**
 * @author arno
 */
public class ClassWithDouble {
    private Double amount;
    private Double withOneFractDigit;
    private Double unqualified;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getWithOneFractDigit() {
        return withOneFractDigit;
    }

    public void setWithOneFractDigit(Double withOneFractDigit) {
        this.withOneFractDigit = withOneFractDigit;
    }

    public Double getUnqualified() {
        return unqualified;
    }

    public void setUnqualified(Double unqualified) {
        this.unqualified = unqualified;
    }
}
