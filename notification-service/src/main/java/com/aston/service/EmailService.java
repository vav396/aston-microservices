package com.aston.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    public void sendCreateAccountEmail(String toEmail) {
        String subject = "Регистрация аккаунта";
        String body = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
        log.info("=== ИМИТАЦИЯ ОТПРАВКИ ПИСЬМА ===");
        log.info("Кому: {}", toEmail);
        log.info("Тема: {}", subject);
        log.info("Текст: {}", body);
        log.info("===============================");
    }

    public void sendDeleteAccountEmail(String toEmail) {
        String subject = "Удаление аккаунта";
        String body = "Здравствуйте! Ваш аккаунт был удалён.";
        log.info("=== ИМИТАЦИЯ ОТПРАВКИ ПИСЬМА ===");
        log.info("Кому: {}", toEmail);
        log.info("Тема: {}", subject);
        log.info("Текст: {}", body);
        log.info("===============================");
    }
}