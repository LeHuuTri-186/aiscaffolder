package com.aiscaffolder.projecttemplateengine.exceptions;

import com.aiscaffolder.projecttemplateengine.controllers.ProjectGenerationController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackageClasses = ProjectGenerationController.class)
public class ProjectGenerationControllerAdvice {
    @Autowired
    ObjectMapper objectMapper;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> enumValidationException(HttpMessageNotReadableException exception) {
        String errorDetails = "Invalid json format";
        if (exception.getCause() instanceof InvalidFormatException ifx) {
            if (ifx.getTargetType() != null && ifx.getTargetType().isEnum()) {
                errorDetails = String.format("Invalid enum value: '%s' for the field: '%s'. The value must be one of: %s.",
                        ifx.getValue(), ifx.getPath().getLast().getFieldName(), Arrays.toString(ifx.getTargetType().getEnumConstants()));
            }
        }

        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(errorDetails).time(LocalDateTime.now()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );


        return ResponseEntity.badRequest().body(ValidationExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(errors).time(LocalDateTime.now()).build());
    }
}
