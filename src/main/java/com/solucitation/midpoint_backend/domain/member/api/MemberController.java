package com.solucitation.midpoint_backend.domain.member.api;

import com.solucitation.midpoint_backend.domain.member.dto.SignupRequestDto;
import com.solucitation.midpoint_backend.domain.member.dto.ValidationErrorResponse;
import com.solucitation.midpoint_backend.domain.member.exception.EmailAlreadyInUseException;
import com.solucitation.midpoint_backend.domain.member.exception.EmailNotVerifiedException;
import com.solucitation.midpoint_backend.domain.member.exception.NicknameAlreadyInUseException;
import com.solucitation.midpoint_backend.domain.member.exception.PasswordMismatchException;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {
    private final MemberService memberService;

    /**
     * 새로운 회원을 등록합니다.
     *
     * @param signupRequestDto 회원가입 요청 DTO
     * @return 성공 시 200 OK와 성공 메시지, 실패 시 400 Bad Request와 일반 오류 메시지 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUpMember(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        memberService.signUpMember(signupRequestDto);
        return ResponseEntity.ok(Collections.singletonMap("message", "회원가입에 성공하였습니다!"));
    }

    @ExceptionHandler({EmailNotVerifiedException.class, NicknameAlreadyInUseException.class, EmailAlreadyInUseException.class, PasswordMismatchException.class})
    public ResponseEntity<ValidationErrorResponse> handleSignUpExceptions(RuntimeException e) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                Collections.singletonList(new ValidationErrorResponse.FieldError(e.getClass().getSimpleName(), e.getMessage()))
        );
        log.error("회원가입 실패: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<ValidationErrorResponse.FieldError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorResponse.FieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(errors);
        log.error("회원가입 실패: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ValidationErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                Collections.singletonList(new ValidationErrorResponse.FieldError("IllegalArgumentException", e.getMessage()))
        );
        log.error("잘못된 요청: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
