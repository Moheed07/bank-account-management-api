package com.moheed.bankapi.controller;

import com.moheed.bankapi.dto.BankDTOs;
import com.moheed.bankapi.model.Account;
import com.moheed.bankapi.model.Transaction;
import com.moheed.bankapi.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Bank Account API", description = "Operations for managing bank accounts and transactions")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ── POST /api/accounts → Create Account ──
    @PostMapping
    @Operation(summary = "Create a new bank account")
    public ResponseEntity<BankDTOs.ApiResponse<Account>> createAccount(
            @Valid @RequestBody BankDTOs.CreateAccountRequest request) {
        Account account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BankDTOs.ApiResponse.success("Account created successfully", account));
    }

    // ── GET /api/accounts → All Accounts ──
    @GetMapping
    @Operation(summary = "Get all active accounts")
    public ResponseEntity<BankDTOs.ApiResponse<List<Account>>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(BankDTOs.ApiResponse.success("Accounts retrieved", accounts));
    }

    // ── GET /api/accounts/{accountNumber} → Single Account ──
    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account details by account number")
    public ResponseEntity<BankDTOs.ApiResponse<Account>> getAccount(
            @PathVariable String accountNumber) {
        Account account = accountService.getAccount(accountNumber);
        return ResponseEntity.ok(BankDTOs.ApiResponse.success("Account found", account));
    }

    // ── POST /api/accounts/{accountNumber}/deposit → Deposit ──
    @PostMapping("/{accountNumber}/deposit")
    @Operation(summary = "Deposit money into an account")
    public ResponseEntity<BankDTOs.ApiResponse<Account>> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody BankDTOs.AmountRequest request) {
        Account account = accountService.deposit(accountNumber, request);
        return ResponseEntity.ok(BankDTOs.ApiResponse.success(
                "Deposit of ₹" + request.getAmount() + " successful", account));
    }

    // ── POST /api/accounts/{accountNumber}/withdraw → Withdraw ──
    @PostMapping("/{accountNumber}/withdraw")
    @Operation(summary = "Withdraw money from an account")
    public ResponseEntity<BankDTOs.ApiResponse<Account>> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody BankDTOs.AmountRequest request) {
        Account account = accountService.withdraw(accountNumber, request);
        return ResponseEntity.ok(BankDTOs.ApiResponse.success(
                "Withdrawal of ₹" + request.getAmount() + " successful", account));
    }

    // ── POST /api/accounts/{accountNumber}/transfer → Transfer ──
    @PostMapping("/{accountNumber}/transfer")
    @Operation(summary = "Transfer funds to another account")
    public ResponseEntity<BankDTOs.ApiResponse<Account>> transfer(
            @PathVariable String accountNumber,
            @Valid @RequestBody BankDTOs.TransferRequest request) {
        Account account = accountService.transfer(accountNumber, request);
        return ResponseEntity.ok(BankDTOs.ApiResponse.success(
                "Transfer of ₹" + request.getAmount() + " successful", account));
    }

    // ── GET /api/accounts/{accountNumber}/transactions → History ──
    @GetMapping("/{accountNumber}/transactions")
    @Operation(summary = "Get transaction history for an account")
    public ResponseEntity<BankDTOs.ApiResponse<List<Transaction>>> getTransactions(
            @PathVariable String accountNumber) {
        List<Transaction> transactions = accountService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(BankDTOs.ApiResponse.success(
                transactions.size() + " transaction(s) found", transactions));
    }

    // ── DELETE /api/accounts/{accountNumber} → Deactivate ──
    @DeleteMapping("/{accountNumber}")
    @Operation(summary = "Deactivate a bank account")
    public ResponseEntity<BankDTOs.ApiResponse<Account>> deactivateAccount(
            @PathVariable String accountNumber) {
        Account account = accountService.deactivateAccount(accountNumber);
        return ResponseEntity.ok(BankDTOs.ApiResponse.success("Account deactivated", account));
    }
}
