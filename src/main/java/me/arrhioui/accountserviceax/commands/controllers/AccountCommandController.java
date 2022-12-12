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
