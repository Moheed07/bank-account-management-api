package com.moheed.bankapi.dto;

import com.moheed.bankapi.model.Account;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

// ─────────────────────────────────────────────
// REQUEST DTOs (what the client sends)
// ─────────────────────────────────────────────

class CreateAccountRequest {
    @NotBlank(message = "Name is required")
    private String accountHolderName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Account type is required (SAVINGS or CURRENT)")
    private Account.AccountType accountType;

    @DecimalMin(value = "100.0", message = "Minimum opening balance is ₹100")
    @NotNull(message = "Initial deposit is required")
    private BigDecimal initialDeposit;

    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String n) { this.accountHolderName = n; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public Account.AccountType getAccountType() { return accountType; }
    public void setAccountType(Account.AccountType t) { this.accountType = t; }
    public BigDecimal getInitialDeposit() { return initialDeposit; }
    public void setInitialDeposit(BigDecimal d) { this.initialDeposit = d; }
}

class AmountRequest {
    @DecimalMin(value = "1.0", message = "Amount must be at least ₹1")
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private String description;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal a) { this.amount = a; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
}

class TransferRequest {
    @NotBlank(message = "Target account number is required")
    private String toAccountNumber;

    @DecimalMin(value = "1.0", message = "Transfer amount must be at least ₹1")
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private String description;

    public String getToAccountNumber() { return toAccountNumber; }
    public void setToAccountNumber(String t) { this.toAccountNumber = t; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal a) { this.amount = a; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
}

// ─────────────────────────────────────────────
// PUBLIC API (expose these classes)
// ─────────────────────────────────────────────

public class BankDTOs {

    public static class CreateAccountRequest extends com.moheed.bankapi.dto.CreateAccountRequest {}
    public static class AmountRequest extends com.moheed.bankapi.dto.AmountRequest {}
    public static class TransferRequest extends com.moheed.bankapi.dto.TransferRequest {}

    // Standard API response wrapper
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public T getData() { return data; }
    }
}
