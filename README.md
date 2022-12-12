## Common API

### Base Command
```java
public class BaseCommand<T> {
    @TargetAggregateIdentifier
    final private T id;

    public BaseCommand(T id) {
        this.id = id;
    }
}
```

### CreateAccountCommand
```java
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
```

### CreateAccountRequestDTO
```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateAccountRequestDTO {
    private String currency;
    private double initialBalance;
}

```

### BaseEvent
```java
@Getter
public class BaseEvent <T>{
    final private T id;

    public BaseEvent(T id) {
        this.id = id;
    }
}

```

### AccountCreatedEvent
```java
@Getter
public class AccountCreatedEvent extends BaseEvent<String>{

    private String currency;
    private double balance;
    private AccountStatus status;

    public AccountCreatedEvent(String id, String currency, double balance, AccountStatus status) {
        super(id);
        this.currency = currency;
        this.balance = balance;
        this.status = status;
    }
}

```

### AccountAggregate
```java
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

```

### AccountCommandController
```java
package me.arrhioui.accountserviceax.commands.controllers;


import lombok.AllArgsConstructor;
import me.arrhioui.accountserviceax.commonapi.commands.CreateAccountCommand;
import me.arrhioui.accountserviceax.commonapi.commands.CreditAccountCommand;
import me.arrhioui.accountserviceax.commonapi.commands.DebitAccountCommand;
import me.arrhioui.accountserviceax.commonapi.dtos.CreateAccountRequestDTO;
import me.arrhioui.accountserviceax.commonapi.dtos.CreditAccountRequestDTO;
import me.arrhioui.accountserviceax.commonapi.dtos.DebitAccountRequestDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping("/commands/account")
@AllArgsConstructor
public class AccountCommandController {

    private CommandGateway commandGateway;
    private EventStore eventStore;

    @PostMapping("/create")
    public CompletableFuture<String> createNewAccount(@RequestBody CreateAccountRequestDTO requestDTO) {
        return commandGateway.send(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                requestDTO.getCurrency(),
                requestDTO.getInitialBalance()
        ));
    }

    @PostMapping("/debit")
    public CompletableFuture<String> debitAccount(@RequestBody DebitAccountRequestDTO requestDTO) {
        return commandGateway.send(new DebitAccountCommand(
                requestDTO.getId(),
                requestDTO.getCurrency(),
                requestDTO.getAmount()
        ));
    }

    @PostMapping("/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDTO requestDTO) {
        return commandGateway.send(new CreditAccountCommand(
                requestDTO.getId(),
                requestDTO.getCurrency(),
                requestDTO.getAmount()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> globalExceptionHandler(Exception e){
        return ResponseEntity.ok(e.getMessage());
    }

    @GetMapping("/eventStore/{id}")
    public Stream eventStore(@PathVariable String id){
        return eventStore.readEvents(id).asStream();
    }
}

```

### AccountEventHandlerService
```java
package me.arrhioui.accountserviceax.query.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.arrhioui.accountserviceax.commonapi.events.AccountCreatedEvent;
import me.arrhioui.accountserviceax.commonapi.events.AccountCreditedEvent;
import me.arrhioui.accountserviceax.query.entities.Account;
import me.arrhioui.accountserviceax.query.queries.GetAllAccounts;
import me.arrhioui.accountserviceax.query.repository.AccountRepository;
import me.arrhioui.accountserviceax.query.repository.AccountTransactionRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AccountEventHandlerService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    @EventHandler
    public void on(AccountCreatedEvent event, EventMessage<AccountCreatedEvent> eventEventMessage){
        Account account = Account
                .builder()
                .createdAt(eventEventMessage.getTimestamp())
                .id(event.getId())
                .balance(event.getBalance())
                .currency(event.getCurrency())
                .status(event.getStatus())
                .build();

        log.info("Account Created");
        accountRepository.save(account);

    }

    @QueryHandler
    public List<Account> on(GetAllAccounts allAccounts){
        return accountRepository.findAll();
    }
}

```

### AccountQueryController
```java
@RestController
@RequestMapping("/query/accounts")
@AllArgsConstructor
public class AccountQueryController {
    private QueryGateway queryGateway;

    @GetMapping("/list")
    public CompletableFuture<List<Account>> accountList(){
         return queryGateway.query(new GetAllAccounts(), ResponseTypes.multipleInstancesOf(Account.class));
    }
}

```
