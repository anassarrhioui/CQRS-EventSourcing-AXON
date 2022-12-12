package me.arrhioui.accountserviceax.query;


import lombok.AllArgsConstructor;
import me.arrhioui.accountserviceax.query.entities.Account;
import me.arrhioui.accountserviceax.query.queries.GetAllAccounts;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
