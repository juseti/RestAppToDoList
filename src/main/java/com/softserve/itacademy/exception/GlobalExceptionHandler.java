package com.softserve.itacademy.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> responseStatusExceptionHandler(HttpServletRequest request, ResponseStatusException exception) {
        saveToLoggerErrorInfo(request, exception);
        return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> accessDeniedExceptionHandler(HttpServletRequest request, AccessDeniedException exception) {
        saveToLoggerErrorInfo(request, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Object> jwtAuthenticationExceptionHandler(HttpServletRequest request, JwtAuthenticationException exception) {
        saveToLoggerErrorInfo(request, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NullEntityReferenceException.class)
    public ResponseEntity<Object> nullEntityReferenceExceptionHandler(HttpServletRequest request, NullEntityReferenceException exception) {
        saveToLoggerErrorInfo(request, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> entityNotFoundExceptionHandler(HttpServletRequest request, EntityNotFoundException exception) {
        saveToLoggerErrorInfo(request, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> internalServerErrorHandler(HttpServletRequest request, Exception exception) {
        saveToLoggerErrorInfo(request, exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void saveToLoggerErrorInfo(HttpServletRequest request, Exception exception) {
        logger.error("Exception raised = {} :: URL = {}", exception.getMessage(), request.getRequestURL());
    }
}
