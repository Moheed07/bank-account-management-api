package com.moheed.bankapi.repository;

import com.moheed.bankapi.model.Account;
import com.moheed.bankapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    // Find all active accounts
    List<Account> findByActiveTrue();

    // Search accounts by name (for admin use)
    @Query("SELECT a FROM Account a WHERE LOWER(a.accountHolderName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Account> searchByName(@Param("name") String name);
}
