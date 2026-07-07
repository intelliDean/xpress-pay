package com.api.xpress.transaction.services;

import com.api.xpress.transaction.data.model.Transaction;
import com.api.xpress.transaction.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
