package com.aston.kafka;

import com.aston.dto.event.UserEventDto;
import com.aston.dto.event.UserOperation;
import com.aston.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

@SpringBootTest
public class NotificationConsumerTest {

    @Autowired
    private NotificationConsumer notificationConsumer;

    @MockBean
    private EmailService emailService;

    @Test
    public void testCreateUserEvent() {
        // 1. Создаем событие CREATE
        UserEventDto event = new UserEventDto(UserOperation.CREATE, "test-create@example.com");

        // 2. Вызываем метод слушателя напрямую (симуляция получения из Kafka)
        notificationConsumer.listen(event);

        // 3. Проверяем, что метод был вызван 1 раз с правильным email
        verify(emailService, times(1)).sendCreateAccountEmail("test-create@example.com");
    }

    @Test
    public void testDeleteUserEvent() {
        // 1. Создаем событие DELETE
        UserEventDto event = new UserEventDto(UserOperation.DELETE, "test-delete@example.com");

        // 2. Вызываем метод слушателя напрямую
        notificationConsumer.listen(event);

        // 3. Проверяем вызов метода для удаления
        verify(emailService, times(1)).sendDeleteAccountEmail("test-delete@example.com");
    }
}