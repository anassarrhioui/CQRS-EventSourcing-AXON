package me.arrhioui.accountserviceax.query.repository;

import me.arrhioui.accountserviceax.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
