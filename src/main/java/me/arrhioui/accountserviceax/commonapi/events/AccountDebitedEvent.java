package me.arrhioui.accountserviceax.commonapi.events;

import lombok.Getter;
import me.arrhioui.accountserviceax.commonapi.enums.AccountStatus;

@Getter
public class AccountDebitedEvent extends BaseEvent<String>{

    private String currency;
    private double amount;

    public AccountDebitedEvent(String id, String currency, double amount) {
        super(id);
        this.currency = currency;
        this.amount = amount;
    }
}
