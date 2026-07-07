package com.api.xpress.transaction.services;

import com.api.xpress.transaction.data.model.Transaction;

public interface TransactionService {
    void saveTransaction(Transaction transaction);
}