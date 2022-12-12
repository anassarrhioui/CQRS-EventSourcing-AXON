package me.arrhioui.accountserviceax.commonapi.commands;

import lombok.Getter;

@Getter
public class CreateAccountCommand extends BaseCommand<String>{

    private String currency;
    private double initialBalance;

    public CreateAccountCommand(String id) {
        super(id);
    }

    public CreateAccountCommand(String id, String currency, double initialBalance) {
        super(id);
        this.currency = currency;
        this.initialBalance = initialBalance;
    }
}
