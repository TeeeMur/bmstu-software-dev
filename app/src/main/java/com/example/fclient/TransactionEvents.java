package com.example.fclient;

public interface TransactionEvents {
    String enterPin(int lastAttempts, String transaction_amount);
    void transactionResult(boolean result);
}
