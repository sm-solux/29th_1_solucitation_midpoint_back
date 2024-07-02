package com.solucitation.midpoint_backend.domain.member.service;

import com.solucitation.midpoint_backend.domain.member.dto.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;

import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final SecureRandom secureRandom = new SecureRandom();

    // 인증코드 이메일 발송
    public String sendVerificationMail(EmailMessage emailMessage, String type) {
        String code = generateVerificationCode();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo()); // 받는 사람
            mimeMessageHelper.setSubject(emailMessage.getSubject()); // 메일 제목
            mimeMessageHelper.setText(setContext(code, type), true); // 메일 본문: setText(setContext(인증코드, html파일명), HTML 여부)
            javaMailSender.send(mimeMessage);

            log.info("Verification email sent successfully to {}", emailMessage.getTo());
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}", emailMessage.getTo(), e);
            throw new RuntimeException("Failed to send verification email", e);
        }
        return code;
    }

    // 영문자, 숫자, 특수문자를 포함한 6자리 인증 코드 생성
    private String generateVerificationCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(chars.length());
            code.append(chars.charAt(randomIndex));
        }
        return code.toString();
    }

    // 메일 형식 생성
    // thymeleaf를 통해 html 적용
    public String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(type, context);
    }
}