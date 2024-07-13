package com.solucitation.midpoint_backend.domain.member.api;

import com.solucitation.midpoint_backend.domain.member.dto.ResetPwRequestDto;
import com.solucitation.midpoint_backend.domain.member.dto.ValidationErrorResponse;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import com.solucitation.midpoint_backend.global.auth.JwtTokenProvider;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class MemberController2 {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Validator validator;

    @GetMapping
    public ResponseEntity<?> getMemberInfo(Authentication authentication) {
        String email = authentication.getName();
        Member member = memberService.getMemberByEmail(email);
        return ResponseEntity.ok(Map.of("message", member.getName()));
    }

    @PostMapping("/reset-pw")
    public ResponseEntity<?> resetPassword(@RequestHeader("Authorization") String token, @RequestBody @Valid ResetPwRequestDto resetPwRequestDto) {
        Set<ConstraintViolation<ResetPwRequestDto>> violations = validator.validate(resetPwRequestDto);
        if (!violations.isEmpty()) {
            List<ValidationErrorResponse.FieldError> fieldErrors = violations.stream()
                    .map(violation -> new ValidationErrorResponse.FieldError(violation.getPropertyPath().toString(), violation.getMessage()))
                    .collect(Collectors.toList());
            ValidationErrorResponse errorResponse = new ValidationErrorResponse(fieldErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        String email = jwtTokenProvider.extractEmailFromToken(token);
        if (email == null || !email.equals(resetPwRequestDto.getEmail())) {
            return ResponseEntity.status(401).body(Map.of("error", "unauthorized", "message", "비밀번호를 재설정할 수 있는 권한이 없습니다."));
        }
        if (!resetPwRequestDto.getNewPassword().equals(resetPwRequestDto.getNewPasswordConfirm())) {
            return ResponseEntity.badRequest().body(Map.of("error", "password_mismatch", "message", "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다."));
        }
        memberService.resetPassword(resetPwRequestDto.getEmail(), resetPwRequestDto.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정이 성공적으로 완료되었습니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<ValidationErrorResponse.FieldError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorResponse.FieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(errors);
        log.error("검증 실패: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        List<ValidationErrorResponse.FieldError> errors = e.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationErrorResponse.FieldError(violation.getPropertyPath().toString(), violation.getMessage()))
                .collect(Collectors.toList());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(errors);
        log.error("검증 실패: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}