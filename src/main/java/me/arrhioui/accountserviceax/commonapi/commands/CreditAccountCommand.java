package me.arrhioui.accountserviceax.commonapi.commands;

import lombok.Getter;

@Getter
public class CreditAccountCommand extends BaseCommand<String>{

    private String currency;
    private double amount;

    public CreditAccountCommand(String id) {
        super(id);
    }

    public CreditAccountCommand(String id, String currency, double amount) {
        super(id);
        this.currency = currency;
        this.amount = amount;
    }
}
