package com.moheed.bankapi.service;

import com.moheed.bankapi.dto.BankDTOs;
import com.moheed.bankapi.exception.BankingExceptions;
import com.moheed.bankapi.model.Account;
import com.moheed.bankapi.model.Transaction;
import com.moheed.bankapi.repository.AccountRepository;
import com.moheed.bankapi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setAccountHolderName("Moheed Nawaaz");
        testAccount.setEmail("moheed@test.com");
        testAccount.setAccountType(Account.AccountType.SAVINGS);
        testAccount.setBalance(new BigDecimal("5000.00"));
        testAccount.setActive(true);
        // Manually set accountNumber since @PrePersist won't fire in unit test
        try {
            var field = Account.class.getDeclaredField("accountNumber");
            field.setAccessible(true);
            field.set(testAccount, "ACC123456");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ────────────────────────────────────────────
    // DEPOSIT TESTS
    // ────────────────────────────────────────────

    @Test
    @DisplayName("Deposit: valid amount should increase balance")
    void deposit_ValidAmount_ShouldIncreaseBalance() {
        when(accountRepository.findByAccountNumber("ACC123456"))
                .thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        BankDTOs.AmountRequest request = new BankDTOs.AmountRequest();
        request.setAmount(new BigDecimal("1000.00"));

        Account result = accountService.deposit("ACC123456", request);

        assertEquals(new BigDecimal("6000.00"), result.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Deposit: inactive account should throw AccountNotActiveException")
    void deposit_InactiveAccount_ShouldThrowException() {
        testAccount.setActive(false);
        when(accountRepository.findByAccountNumber("ACC123456"))
                .thenReturn(Optional.of(testAccount));

        BankDTOs.AmountRequest request = new BankDTOs.AmountRequest();
        request.setAmount(new BigDecimal("500.00"));

        assertThrows(BankingExceptions.AccountNotActiveException.class,
                () -> accountService.deposit("ACC123456", request));
    }

    // ────────────────────────────────────────────
    // WITHDRAWAL TESTS
    // ────────────────────────────────────────────

    @Test
    @DisplayName("Withdraw: valid amount should decrease balance")
    void withdraw_ValidAmount_ShouldDecreaseBalance() {
        when(accountRepository.findByAccountNumber("ACC123456"))
                .thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        BankDTOs.AmountRequest request = new BankDTOs.AmountRequest();
        request.setAmount(new BigDecimal("2000.00"));

        Account result = accountService.withdraw("ACC123456", request);

        assertEquals(new BigDecimal("3000.00"), result.getBalance());
    }

    @Test
    @DisplayName("Withdraw: insufficient funds should throw InsufficientFundsException")
    void withdraw_InsufficientFunds_ShouldThrowException() {
        when(accountRepository.findByAccountNumber("ACC123456"))
                .thenReturn(Optional.of(testAccount));

        BankDTOs.AmountRequest request = new BankDTOs.AmountRequest();
        request.setAmount(new BigDecimal("9999.00")); // More than balance

        assertThrows(BankingExceptions.InsufficientFundsException.class,
                () -> accountService.withdraw("ACC123456", request));
    }

    @Test
    @DisplayName("Withdraw: account not found should throw AccountNotFoundException")
    void withdraw_AccountNotFound_ShouldThrowException() {
        when(accountRepository.findByAccountNumber("INVALID"))
                .thenReturn(Optional.empty());

        BankDTOs.AmountRequest request = new BankDTOs.AmountRequest();
        request.setAmount(new BigDecimal("100.00"));

        assertThrows(BankingExceptions.AccountNotFoundException.class,
                () -> accountService.withdraw("INVALID", request));
    }

    // ────────────────────────────────────────────
    // TRANSFER TESTS
    // ────────────────────────────────────────────

    @Test
    @DisplayName("Transfer: self-transfer should throw SelfTransferException")
    void transfer_SelfTransfer_ShouldThrowException() {
        BankDTOs.TransferRequest request = new BankDTOs.TransferRequest();
        request.setToAccountNumber("ACC123456"); // Same as source
        request.setAmount(new BigDecimal("100.00"));

        assertThrows(BankingExceptions.SelfTransferException.class,
                () -> accountService.transfer("ACC123456", request));
    }

    @Test
    @DisplayName("Transfer: valid transfer should update both account balances")
    void transfer_ValidTransfer_ShouldUpdateBothBalances() {
        Account receiverAccount = new Account();
        receiverAccount.setBalance(new BigDecimal("1000.00"));
        receiverAccount.setActive(true);
        try {
            var field = Account.class.getDeclaredField("accountNumber");
            field.setAccessible(true);
            field.set(receiverAccount, "ACC789");
        } catch (Exception e) { throw new RuntimeException(e); }

        when(accountRepository.findByAccountNumber("ACC123456"))
                .thenReturn(Optional.of(testAccount));
        when(accountRepository.findByAccountNumber("ACC789"))
                .thenReturn(Optional.of(receiverAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        BankDTOs.TransferRequest request = new BankDTOs.TransferRequest();
        request.setToAccountNumber("ACC789");
        request.setAmount(new BigDecimal("2000.00"));

        accountService.transfer("ACC123456", request);

        assertEquals(new BigDecimal("3000.00"), testAccount.getBalance());
        assertEquals(new BigDecimal("3000.00"), receiverAccount.getBalance());
        verify(transactionRepository, times(2)).save(any(Transaction.class)); // debit + credit
    }
}
