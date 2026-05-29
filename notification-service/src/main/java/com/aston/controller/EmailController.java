package com.aston.controller;

import com.aston.service.EmailService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // DTO для входящего запроса
    @Data
    public static class SendEmailRequest {
        private String email;
        private String operation;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody SendEmailRequest request) {
        if ("CREATE".equalsIgnoreCase(request.getOperation())) {
            emailService.sendCreateAccountEmail(request.getEmail());
            return ResponseEntity.ok("Письмо о создании аккаунта отправлено.");
        } else if ("DELETE".equalsIgnoreCase(request.getOperation())) {
            emailService.sendDeleteAccountEmail(request.getEmail());
            return ResponseEntity.ok("Письмо об удалении аккаунта отправлено.");
        } else {
            return ResponseEntity.badRequest().body("Неизвестная операция: " + request.getOperation());
        }
    }
}