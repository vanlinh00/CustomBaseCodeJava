package com.example.basecommon.exception;

import com.example.basecommon.enums.MessageCode;
import com.example.basecommon.factory.ResponseFactory;
import com.example.basecommon.util.BaseUtils;
import jakarta.servlet.http.HttpServletRequest;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.ArrayList;
import java.util.List;





@ControllerAdvice
@Slf4j
public class ExceptionInterceptor {




    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(HttpServletRequest httpServletRequest, Exception e) throws Exception {
        if (AnnotationUtils.findAnnotation(e.getClass(), org.springframework.web.bind.annotation.ResponseStatus.class) != null) {
            throw e;
        }
        if (e instanceof BasicException) {
            BasicException ex = (BasicException) e;


            List<String> errors = ex.getErrors();
            String code = (errors != null && !errors.isEmpty()) ? MessageCode.BAD_REQUEST.getCode()
                    : ex.getMessageCode().getCode();
            if (errors != null && !errors.isEmpty()) {
                log.error("Validation error occurred: {}", errors);
            }
            String message = BaseUtils.getErrorMessageLanguage(httpServletRequest, code);


            return ResponseFactory.error(code, message, errors);
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;


            List<String> details = new ArrayList<>();
            for (ObjectError error : ex.getBindingResult().getAllErrors()) {
                FieldError fieldError = (FieldError) error;


                String defaultMessage = error.getDefaultMessage();
                String message = BaseUtils.getErrorMessageLanguage(httpServletRequest, defaultMessage);
                if (StringUtils.isNotEmpty(message)) {
                    details.add(String.format("%s: %s", fieldError.getField(), message));
                } else {
                    details.add(String.format("%s: %s", fieldError.getField(), defaultMessage));
                }
            }


            String code = MessageCode.INVALID_ARGUMENT.getCode();
            String message = BaseUtils.getErrorMessageLanguage(httpServletRequest, code);
            return ResponseFactory.error(code, message, details);
        } else if (e instanceof AccessDeniedException) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } else if (e.getCause() != null && e.getCause() instanceof BasicException) {
            BasicException baseException = (BasicException) e.getCause();
            MessageCode messageCode = baseException.getMessageCode();
            String code = messageCode.getCode();
            String message = baseException.getMessage();
            if (StringUtils.isEmpty(message)) {
                message = BaseUtils.getErrorMessageLanguage(httpServletRequest, code);
            }
            return ResponseFactory.error(code, message);
        } else if (e instanceof BadCredentialsException) {
            String code = MessageCode.ACCOUNT_INCORRECT.getCode();
            String message = BaseUtils.getErrorMessageLanguage(httpServletRequest, code);
            return ResponseFactory.error(code, message);
        } else if (e instanceof JwtValidationException) {
            String code = MessageCode.INVALID_TOKEN.getCode();
            String message = BaseUtils.getErrorMessageLanguage(httpServletRequest, code);
            return ResponseFactory.error(HttpStatus.FORBIDDEN, code, message, List.of(e.getMessage()));
        }


        log.error(e.getMessage(), e);


        String code = MessageCode.SYSTEM_ERROR.getCode();
        String message = BaseUtils.getErrorMessageLanguage(httpServletRequest, code);
        return ResponseFactory.error(code, message, e.getMessage());
    }
}

