package com.ddi.assessment.news.api;

import com.ddi.assessment.news.api.dto.ResultResponse;
import com.ddi.assessment.news.domain.auth.exception.RefreshTokenRotationException;
import com.ddi.assessment.news.domain.collectrule.exception.DuplicateCollectRuleException;
import com.ddi.assessment.news.domain.user.exception.UserLoginException;
import com.ddi.assessment.news.domain.user.exception.UserRegisterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultResponse<Void>> handleException(Exception ex) {
        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultResponse.failure("알 수 없는 오류 발생."));
    }

    @ExceptionHandler(UserLoginException.class)
    public ResponseEntity<ResultResponse<Void>> handleUserLoginException(UserLoginException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResultResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(RefreshTokenRotationException.class)
    public ResponseEntity<ResultResponse<Void>> handleRefreshTokenReplay(RefreshTokenRotationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResultResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(UserRegisterException.class)
    public ResponseEntity<ResultResponse<Void>> handleUserRegisterException(UserRegisterException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResultResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateCollectRuleException.class)
    public ResponseEntity<ResultResponse<Void>> handleDuplicateSchedulerException(DuplicateCollectRuleException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResultResponse.failure("이미 존재하는 스케줄 입니다."));
    }

    // @Valid 유효성 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResultResponse.failure(errorMessage));
    }

}
