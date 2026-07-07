package com.api.xpress.transaction.repository;

import com.api.xpress.transaction.data.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
