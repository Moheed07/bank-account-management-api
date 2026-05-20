package com.moheed.bankapi.service;

import com.moheed.bankapi.dto.BankDTOs;
import com.moheed.bankapi.exception.BankingExceptions;
import com.moheed.bankapi.model.Account;
import com.moheed.bankapi.model.Transaction;
import com.moheed.bankapi.repository.AccountRepository;
import com.moheed.bankapi.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // ── CREATE ACCOUNT ──
    @Transactional
    public Account createAccount(BankDTOs.CreateAccountRequest request) {
        // Business rule: no duplicate emails
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new BankingExceptions.DuplicateAccountException(request.getEmail());
        }

        Account account = new Account();
        account.setAccountHolderName(request.getAccountHolderName());
        account.setEmail(request.getEmail());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialDeposit());

        Account saved = accountRepository.save(account);

        // Record the initial deposit as a transaction
        recordTransaction(saved, Transaction.TransactionType.DEPOSIT,
                request.getInitialDeposit(), saved.getBalance(), null, "Initial deposit");

        return saved;
    }

    // ── GET ACCOUNT ──
    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankingExceptions.AccountNotFoundException(accountNumber));
    }

    // ── GET ALL ACCOUNTS ──
    public List<Account> getAllAccounts() {
        return accountRepository.findByActiveTrue();
    }

    // ── DEPOSIT ──
    @Transactional
    public Account deposit(String accountNumber, BankDTOs.AmountRequest request) {
        Account account = getActiveAccount(accountNumber);

        account.setBalance(account.getBalance().add(request.getAmount()));
        Account updated = accountRepository.save(account);

        recordTransaction(updated, Transaction.TransactionType.DEPOSIT,
                request.getAmount(), updated.getBalance(), null,
                request.getDescription() != null ? request.getDescription() : "Deposit");

        return updated;
    }

    // ── WITHDRAW ──
    @Transactional
    public Account withdraw(String accountNumber, BankDTOs.AmountRequest request) {
        Account account = getActiveAccount(accountNumber);

        // Business rule: check sufficient balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BankingExceptions.InsufficientFundsException(
                    accountNumber,
                    account.getBalance().doubleValue(),
                    request.getAmount().doubleValue()
            );
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        Account updated = accountRepository.save(account);

        recordTransaction(updated, Transaction.TransactionType.WITHDRAWAL,
                request.getAmount(), updated.getBalance(), null,
                request.getDescription() != null ? request.getDescription() : "Withdrawal");

        return updated;
    }

    // ── TRANSFER ──
    @Transactional
    public Account transfer(String fromAccountNumber, BankDTOs.TransferRequest request) {
        // Business rule: no self-transfer
        if (fromAccountNumber.equals(request.getToAccountNumber())) {
            throw new BankingExceptions.SelfTransferException();
        }

        Account fromAccount = getActiveAccount(fromAccountNumber);
        Account toAccount = getActiveAccount(request.getToAccountNumber());

        // Business rule: check sufficient balance
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BankingExceptions.InsufficientFundsException(
                    fromAccountNumber,
                    fromAccount.getBalance().doubleValue(),
                    request.getAmount().doubleValue()
            );
        }

        String desc = request.getDescription() != null ? request.getDescription() : "Fund Transfer";

        // Debit sender
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(fromAccount);
        recordTransaction(fromAccount, Transaction.TransactionType.TRANSFER_DEBIT,
                request.getAmount(), fromAccount.getBalance(),
                request.getToAccountNumber(), "Transfer to " + request.getToAccountNumber() + " | " + desc);

        // Credit receiver
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(toAccount);
        recordTransaction(toAccount, Transaction.TransactionType.TRANSFER_CREDIT,
                request.getAmount(), toAccount.getBalance(),
                fromAccountNumber, "Transfer from " + fromAccountNumber + " | " + desc);

        return fromAccount;
    }

    // ── GET TRANSACTION HISTORY ──
    public List<Transaction> getTransactionHistory(String accountNumber) {
        Account account = getAccount(accountNumber);
        return transactionRepository.findByAccountOrderByCreatedAtDesc(account);
    }

    // ── DEACTIVATE ACCOUNT ──
    @Transactional
    public Account deactivateAccount(String accountNumber) {
        Account account = getAccount(accountNumber);
        account.setActive(false);
        return accountRepository.save(account);
    }

    // ── HELPER: ensure account is active ──
    private Account getActiveAccount(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (!account.isActive()) {
            throw new BankingExceptions.AccountNotActiveException(accountNumber);
        }
        return account;
    }

    // ── HELPER: save a transaction record ──
    private void recordTransaction(Account account, Transaction.TransactionType type,
                                   BigDecimal amount, BigDecimal balanceAfter,
                                   String relatedAccount, String description) {
        Transaction txn = new Transaction();
        txn.setAccount(account);
        txn.setType(type);
        txn.setAmount(amount);
        txn.setBalanceAfter(balanceAfter);
        txn.setRelatedAccountNumber(relatedAccount);
        txn.setDescription(description);
        transactionRepository.save(txn);
    }
}
