package uk.gov.hmcts.reform.hmc.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_HEARING_PAYLOAD;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.debug("HttpMessageNotReadableException:{}", ex.getLocalizedMessage());
        return toResponseEntity(status, INVALID_HEARING_PAYLOAD);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        log.debug("BadRequestException:{}", ex.getLocalizedMessage());
        return toResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.debug("ResourceNotFoundException:{}", ex.getLocalizedMessage());
        return toResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String[] errors = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError:: getDefaultMessage)
            .toArray(String[]::new);
        log.debug("MethodArgumentNotValidException:{}", ex.getMessage());
        return toResponseEntity(status, errors);
    }

    private ResponseEntity<Object> toResponseEntity(HttpStatusCode status, String... errors) {
        var apiError = new ApiError(status, errors == null ? null : List.of(errors));
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

}
