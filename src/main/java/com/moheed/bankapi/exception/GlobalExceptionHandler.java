package com.moheed.bankapi.exception;

import com.moheed.bankapi.dto.BankDTOs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BankingExceptions.AccountNotFoundException.class)
    public ResponseEntity<BankDTOs.ApiResponse<Void>> handleAccountNotFound(
            BankingExceptions.AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BankDTOs.ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BankingExceptions.InsufficientFundsException.class)
    public ResponseEntity<BankDTOs.ApiResponse<Void>> handleInsufficientFunds(
            BankingExceptions.InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BankDTOs.ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BankingExceptions.AccountNotActiveException.class)
    public ResponseEntity<BankDTOs.ApiResponse<Void>> handleAccountNotActive(
            BankingExceptions.AccountNotActiveException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BankDTOs.ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BankingExceptions.DuplicateAccountException.class)
    public ResponseEntity<BankDTOs.ApiResponse<Void>> handleDuplicate(
            BankingExceptions.DuplicateAccountException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(BankDTOs.ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BankingExceptions.SelfTransferException.class)
    public ResponseEntity<BankDTOs.ApiResponse<Void>> handleSelfTransfer(
            BankingExceptions.SelfTransferException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BankDTOs.ApiResponse.error(ex.getMessage()));
    }

    // Handles @Valid failures — returns field-level errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BankDTOs.ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BankDTOs.ApiResponse<>(false, "Validation failed", errors));
    }

    // Catch-all for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BankDTOs.ApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BankDTOs.ApiResponse.error("An unexpected error occurred: " + ex.getMessage()));
    }
}
