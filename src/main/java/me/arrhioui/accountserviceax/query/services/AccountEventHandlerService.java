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
