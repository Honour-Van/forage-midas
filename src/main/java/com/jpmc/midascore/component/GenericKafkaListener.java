package com.jpmc.midascore.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import com.jpmc.midascore.service.UserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 通用 Kafka 消息监听器
 */
@Component
public class GenericKafkaListener {
    private static final Logger logger = LoggerFactory.getLogger(GenericKafkaListener.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserService userService;
    private final TransactionService transactionService;

    public GenericKafkaListener(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    /**
     * 通用监听函数，可以监听多个 topic
     *
     * 注意：
     * - topics 可以是配置项，也可以直接写死多个。
     * - containerFactory 可指定不同类型的反序列化策略（如 JSON、String）。
     */
    @KafkaListener(
            topics = {"${kafka.topic.transactions}"}, // 可监听多个topic
            groupId = "${spring.kafka.consumer.group-id:default-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(@Payload String message, ConsumerRecord<String, String> record) {
        try {
            logger.info("📥 Received message from topic [{}]: {}", record.topic(), message);
            Transaction tx = objectMapper.readValue(message, Transaction.class);
            handleTransaction(tx);
        } catch (Exception e) {
            logger.error("❌ Failed to process message: {}", message, e);
        }
    }

    private void handleTransaction(Transaction transaction) {
        long senderId = transaction.getSenderId(), recipientId = transaction.getRecipientId();

        logger.debug("sender[{}]: {}, {}; recipient[{}], {}, {}", senderId,
                userService.getUserName(senderId), userService.getBalance(senderId),
                recipientId, userService.getUserName(recipientId), userService.getBalance(recipientId));

        float senderAmount = userService.getBalance(senderId);
        if (senderAmount < transaction.getAmount()) {
            logger.error("sender has no enough money");
        }
        else {
            transactionService.insertOneRecord(transaction);
            userService.updateBalance(senderId, senderAmount - transaction.getAmount());
            userService.updateBalance(recipientId, userService.getBalance(recipientId) + transaction.getAmount());
            logger.debug("sender[{}]: {}, {}; recipient[{}], {}, {}", senderId,
                    userService.getUserName(senderId), userService.getBalance(senderId),
                    recipientId, userService.getUserName(recipientId), userService.getBalance(recipientId));
        }

    }

}
