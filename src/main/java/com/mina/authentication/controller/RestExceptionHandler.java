package com.mina.authentication.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.mina.authentication.controller.dto.ApiErrorResponse;
import com.mina.authentication.exceptions.DuplicateException;
import com.mina.authentication.exceptions.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException e) {
    return ResponseEntity.status(NOT_FOUND).body(new ApiErrorResponse(NOT_FOUND.value(), e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleRequestNotValidException(MethodArgumentNotValidException e) {

    List<String> errors = new ArrayList<>();
    e.getBindingResult()
        .getFieldErrors().forEach(error -> errors.add(error.getField() + ": " + error.getDefaultMessage()));
    e.getBindingResult()
        .getGlobalErrors() //Global errors are not associated with a specific field but are related to the entire object being validated.
        .forEach(error -> errors.add(error.getObjectName() + ": " + error.getDefaultMessage()));

    String message = "Validation of request failed: %s".formatted(String.join(", ", errors));
    return ResponseEntity.status(BAD_REQUEST).body(new ApiErrorResponse(BAD_REQUEST.value(), message));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiErrorResponse> handleBadCredentialsException() {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(new ApiErrorResponse(UNAUTHORIZED.value(), "Invalid username or password"));
  }

  @ExceptionHandler(DuplicateException.class)
  public ResponseEntity<ApiErrorResponse> handleDuplicateException(DuplicateException e) {
    return ResponseEntity.status(CONFLICT).body(new ApiErrorResponse(CONFLICT.value(), e.getMessage()));
  }

  @ExceptionHandler(InternalAuthenticationServiceException.class)
  public ResponseEntity<ApiErrorResponse> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
    return ResponseEntity.status(UNAUTHORIZED).body(new ApiErrorResponse(UNAUTHORIZED.value(), e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleUnknownException(Exception e) {
    return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(INTERNAL_SERVER_ERROR.value(), e.getMessage()));
  }

}
