package me.arrhioui.accountserviceax.commands.aggregates;

import lombok.NoArgsConstructor;
import me.arrhioui.accountserviceax.commonapi.commands.CreateAccountCommand;
import me.arrhioui.accountserviceax.commonapi.commands.CreditAccountCommand;
import me.arrhioui.accountserviceax.commonapi.commands.DebitAccountCommand;
import me.arrhioui.accountserviceax.commonapi.enums.AccountStatus;
import me.arrhioui.accountserviceax.commonapi.events.AccountCreatedEvent;
import me.arrhioui.accountserviceax.commonapi.events.AccountCreditedEvent;
import me.arrhioui.accountserviceax.commonapi.events.AccountDebitedEvent;
import me.arrhioui.accountserviceax.commonapi.exceptions.NegativeBalanceException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class AccountAggregate {
    @AggregateIdentifier
    private String id;
    private String currency;
    private double balance;
    private AccountStatus status;


    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        if (createAccountCommand.getInitialBalance() < 0) {
            throw new NegativeBalanceException("Negative Initial Balance");
        }

        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getCurrency(),
                createAccountCommand.getInitialBalance(),
                AccountStatus.CREATED
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.id = event.getId();
        this.currency = event.getCurrency();
        this.balance = event.getBalance();
        this.status = event.getStatus();
    }

    @CommandHandler
    public void handle(CreditAccountCommand creditAccountCommand) {
        if (creditAccountCommand.getAmount() < 0) {
            throw new NegativeBalanceException("Negative Initial Balance");
        }

        AggregateLifecycle.apply(new AccountCreditedEvent(
                creditAccountCommand.getId(),
                creditAccountCommand.getCurrency(),
                creditAccountCommand.getAmount()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        this.balance += event.getAmount();
    }

    @CommandHandler
    public void handle(DebitAccountCommand debitAccountCommand) {
        if (debitAccountCommand.getAmount() < 0) {
            throw new NegativeBalanceException("Negative Initial Balance");
        }

        if (debitAccountCommand.getAmount() > this.balance) {
            throw new NegativeBalanceException("Negative Initial Balance");
        }

        AggregateLifecycle.apply(new AccountDebitedEvent(
                debitAccountCommand.getId(),
                debitAccountCommand.getCurrency(),
                debitAccountCommand.getAmount()
        ));
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        this.balance -= event.getAmount();
    }
}
