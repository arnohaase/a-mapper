package com.ajjpj.amapper.examples.context;


import java.util.Currency;

/**
 * @author arno
 */
public class NormalizedInvoice implements CurrencyProvider {
    private Currency currency;
    // this would normally be a list, but we want to avoid the complexity of
    //  collection mapping, and a single position suffices to illustrate
    //  the concept of 'context'
    private NormalizedPosition position = new NormalizedPosition();

    @Override
    public Currency getCurrency() {
        return currency;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public NormalizedPosition getPosition() {
        return position;
    }
    public void setPosition(NormalizedPosition position) {
        this.position = position;
    }
}
