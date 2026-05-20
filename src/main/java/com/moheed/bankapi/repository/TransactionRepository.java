package com.moheed.bankapi.repository;

import com.moheed.bankapi.model.Account;
import com.moheed.bankapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // All transactions for an account, newest first
    List<Transaction> findByAccountOrderByCreatedAtDesc(Account account);

    // Transactions within a date range
    @Query("SELECT t FROM Transaction t WHERE t.account = :account " +
           "AND t.createdAt BETWEEN :from AND :to ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountAndDateRange(
        @Param("account") Account account,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    // Last N transactions for an account
    @Query("SELECT t FROM Transaction t WHERE t.account = :account ORDER BY t.createdAt DESC LIMIT :limit")
    List<Transaction> findRecentTransactions(
        @Param("account") Account account,
        @Param("limit") int limit
    );
}
