package me.arrhioui.accountserviceax.commonapi.events;

import lombok.Getter;

@Getter
public class AccountCreditedEvent extends BaseEvent<String>{

    private String currency;
    private double amount;

    public AccountCreditedEvent(String id, String currency, double amount) {
        super(id);
        this.currency = currency;
        this.amount = amount;
    }
}
