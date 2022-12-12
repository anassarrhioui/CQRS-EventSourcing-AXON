package me.arrhioui.accountserviceax.query.repository;

import me.arrhioui.accountserviceax.query.entities.Account;
import me.arrhioui.accountserviceax.query.entities.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, String> {
}
