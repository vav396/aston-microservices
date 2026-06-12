package com.aston.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @CircuitBreaker(name = "emailService", fallbackMethod = "sendCreateAccountEmailFallback")
    public void sendCreateAccountEmail(String toEmail) {
        String subject = "Регистрация аккаунта";
        String body = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
        log.info("=== ИМИТАЦИЯ ОТПРАВКИ ПИСЬМА ===");
        log.info("Кому: {}", toEmail);
        log.info("Тема: {}", subject);
        log.info("Текст: {}", body);
        log.info("===============================");
    }

    public void sendCreateAccountEmailFallback(String toEmail, Throwable throwable) {
        log.error("Ошибка при отправке письма о регистрации {}: {}", toEmail, throwable.getMessage());
        log.info("[FALLBACK] Имитация отправки письма о регистрации на {}", toEmail);
    }

    @CircuitBreaker(name = "emailService", fallbackMethod = "sendDeleteAccountEmailFallback")
    public void sendDeleteAccountEmail(String toEmail) {
        String subject = "Удаление аккаунта";
        String body = "Здравствуйте! Ваш аккаунт был удалён.";
        log.info("=== ИМИТАЦИЯ ОТПРАВКИ ПИСЬМА ===");
        log.info("Кому: {}", toEmail);
        log.info("Тема: {}", subject);
        log.info("Текст: {}", body);
        log.info("===============================");
    }

    public void sendDeleteAccountEmailFallback(String toEmail, Throwable throwable) {
        log.error("Ошибка при отправке письма об удалении {}: {}", toEmail, throwable.getMessage());
        log.info("[FALLBACK] Имитация отправки письма об удалении на {}", toEmail);
    }
}