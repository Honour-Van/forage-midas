package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository repository;
    public TransactionService(TransactionRepository transactionRepository) {
        this.repository = transactionRepository;
    }

    public void insertOneRecord(Transaction transaction) {
        TransactionRecord record = new TransactionRecord(transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());
        repository.save(record);
    }
}
