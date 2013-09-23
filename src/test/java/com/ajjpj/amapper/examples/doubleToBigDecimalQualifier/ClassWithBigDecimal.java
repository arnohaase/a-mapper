package com.ajjpj.amapper.examples.doubleToBigDecimalQualifier;

import java.math.BigDecimal;

/**
 * @author arno
 */
public class ClassWithBigDecimal {
    private BigDecimal amount;
    private BigDecimal withOneFactDigit;
    private BigDecimal unqualified;

    public BigDecimal getAmount() {
        return amount;
    }

    @TwoDigitsRoundEven
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getWithOneFractDigit() {
        return withOneFactDigit;
    }

    @NDigitsRound("1")
    public void setWithOneFractDigit(BigDecimal withOneFactDigit) {
        this.withOneFactDigit = withOneFactDigit;
    }

    public BigDecimal getUnqualified() {
        return unqualified;
    }

    public void setUnqualified(BigDecimal unqualified) {
        this.unqualified = unqualified;
    }
}
