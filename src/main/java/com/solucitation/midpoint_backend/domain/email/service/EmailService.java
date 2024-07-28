package com.solucitation.midpoint_backend.domain.email.service;

import com.solucitation.midpoint_backend.domain.email.dto.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, Boolean> verifiedEmails = new ConcurrentHashMap<>();

    // 인증 시간을 저장하는 Map 추가
    private final Map<String, LocalDateTime> verifiedEmailTimestamps = new ConcurrentHashMap<>();

    private static final int EXPIRATION_TIME_MINUTES = 1; // 인증코드는 유효시간 4분을 갖는다. 4분이 지난 인증코드는 검증할 수 없음
    private static final int VERIFICATION_VALID_MINUTES = 2; // 인증검증 후 유효시간 30분을 갖는다.

    // 인증코드 이메일 발송
    public String sendVerificationMail(EmailMessage emailMessage, String type) {
        String code = generateVerificationCode();
        LocalDateTime now = LocalDateTime.now();

        // 새로운 인증 코드를 추가하며 기존 인증 코드를 무효화
        verificationCodes.put(emailMessage.getTo(), new VerificationCode(code, now.plusMinutes(EXPIRATION_TIME_MINUTES)));

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo()); // 받는 사람
            mimeMessageHelper.setSubject(emailMessage.getSubject()); // 메일 제목
            mimeMessageHelper.setText(setContext(code, type), true); // 메일 본문: setText(setContext(인증코드, html파일명), HTML 여부)
            javaMailSender.send(mimeMessage);

            log.info("인증코드가 성공적으로 전송되었습니다. 이메일: {}", emailMessage.getTo());
        } catch (MessagingException e) {
            log.error("인증코드 전송 실패했습니다. 이메일: {}", emailMessage.getTo(), e);
            throw new RuntimeException("인증코드 전송 실패했습니다.", e);
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

    // 인증코드 검증 및 인증 시간 기록
    public boolean verifyCode(String email, String code) {
        VerificationCode verificationCode = verificationCodes.get(email);

        if (verificationCode == null || verificationCode.expiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        boolean isValid = verificationCode.code().equals(code);
        if (isValid) {
            verifiedEmails.put(email, true);
            // 인증 시간을 기록
            verifiedEmailTimestamps.put(email, LocalDateTime.now());
        }
        return isValid;
    }

    // 이메일이 인증되었는지 확인
    public boolean isEmailVerified(String email) {
        LocalDateTime lastVerifiedTime = verifiedEmailTimestamps.get(email);
        if (lastVerifiedTime == null) {
            return false;
        }
        // 검증된 이메일인지 확인
        boolean isVerified = verifiedEmails.getOrDefault(email, false);
        // 인증 후 30분이 지났는지 확인
        boolean isRecentlyVerified = lastVerifiedTime.isAfter(LocalDateTime.now().minusMinutes(VERIFICATION_VALID_MINUTES));
        return isVerified && isRecentlyVerified;
    }

    private record VerificationCode(String code, LocalDateTime expiresAt) {}
}