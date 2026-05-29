package com.aston.kafka;

import com.aston.dto.event.UserEventDto;
import com.aston.dto.event.UserOperation;
import com.aston.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void listen(UserEventDto event) {
        log.info("Получено событие из Kafka: {}", event);

        // Используем Enum для проверки, это надежнее строк
        if (UserOperation.CREATE.equals(event.getOperation())) {
            emailService.sendCreateAccountEmail(event.getEmail());
        } else if (UserOperation.DELETE.equals(event.getOperation())) {
            emailService.sendDeleteAccountEmail(event.getEmail());
        } else {
            log.warn("Неизвестная операция: {}", event.getOperation());
        }
    }
}