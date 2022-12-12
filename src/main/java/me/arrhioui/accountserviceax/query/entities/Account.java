package me.arrhioui.accountserviceax.query.entities;

import lombok.*;
import me.arrhioui.accountserviceax.commonapi.enums.AccountStatus;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Account {


    @Id
    private String id;
    private Instant createdAt;
    private String currency;
    private double balance;
    private AccountStatus status;

    @OneToMany(mappedBy = "account")
    private List<AccountTransaction> transactions = new ArrayList<>();
}
