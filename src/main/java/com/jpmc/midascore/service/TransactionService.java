package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class.getName());

    private final TransactionRepository repository;
    private final UserService userService;
    private final IncentiveService incentiveService;

    public TransactionService(TransactionRepository transactionRepository, UserService userService, IncentiveService incentiveService) {
        this.repository = transactionRepository;
        this.userService = userService;
        this.incentiveService = incentiveService;
    }

    public void insertOneRecord(Transaction transaction) {
        TransactionRecord record = new TransactionRecord(transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());
        repository.save(record);
    }

    public void transfer(Transaction transaction) {
        long senderId = transaction.getSenderId(), recipientId = transaction.getRecipientId();

        logger.debug("sender[{}]: {}, {}; recipient[{}], {}, {}", senderId,
                userService.getUserName(senderId), userService.getBalance(senderId),
                recipientId, userService.getUserName(recipientId), userService.getBalance(recipientId));

        float senderAmount = userService.getBalance(senderId);
        if (senderAmount < transaction.getAmount()) {
            logger.error("sender has no enough money");
        }
        else {
            Incentive incentive = incentiveService.postTransaction(transaction);
            insertOneRecord(transaction);
            userService.updateBalance(senderId, senderAmount - transaction.getAmount());
            userService.updateBalance(recipientId, userService.getBalance(recipientId) + transaction.getAmount() + incentive.getAmount());
            logger.debug("sender[{}]: {}, {}; recipient[{}], {}, {}; incentive: {}", senderId,
                    userService.getUserName(senderId), userService.getBalance(senderId),
                    recipientId, userService.getUserName(recipientId), userService.getBalance(recipientId), incentive.getAmount());
        }
    }
}
