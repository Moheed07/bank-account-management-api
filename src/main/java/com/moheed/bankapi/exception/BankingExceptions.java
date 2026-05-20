package com.moheed.bankapi.exception;

// ── Custom Business Exceptions ──

public class BankingExceptions {

    // Account not found
    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String accountNumber) {
            super("Account not found: " + accountNumber);
        }
    }

    // Insufficient balance for withdrawal or transfer
    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String accountNumber, double available, double requested) {
            super(String.format(
                "Insufficient funds in account %s. Available: ₹%.2f, Requested: ₹%.2f",
                accountNumber, available, requested
            ));
        }
    }

    // Trying to operate on a deactivated account
    public static class AccountNotActiveException extends RuntimeException {
        public AccountNotActiveException(String accountNumber) {
            super("Account " + accountNumber + " is not active.");
        }
    }

    // Duplicate account (email already exists)
    public static class DuplicateAccountException extends RuntimeException {
        public DuplicateAccountException(String email) {
            super("An account with email " + email + " already exists.");
        }
    }

    // Transfer to self
    public static class SelfTransferException extends RuntimeException {
        public SelfTransferException() {
            super("Cannot transfer funds to the same account.");
        }
    }
}
