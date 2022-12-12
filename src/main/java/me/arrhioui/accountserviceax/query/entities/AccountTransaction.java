package me.arrhioui.accountserviceax.query.entities;

import me.arrhioui.accountserviceax.commonapi.enums.TransactionType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class AccountTransaction {
    @Id
    private Long id;
    private Date timestamp;
    private double amount;
    private TransactionType transactionType;
    @ManyToOne
    private Account account;
}
