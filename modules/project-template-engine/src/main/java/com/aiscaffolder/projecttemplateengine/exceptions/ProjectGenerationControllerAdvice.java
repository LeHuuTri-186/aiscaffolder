package com.aiscaffolder.projecttemplateengine.exceptions;

import com.aiscaffolder.projecttemplateengine.controllers.ProjectGenerationController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestControllerAdvice(basePackageClasses = ProjectGenerationController.class)
public class ProjectGenerationControllerAdvice {
    final ObjectMapper objectMapper;

    public ProjectGenerationControllerAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(DuplicateEntity.class)
    public ResponseEntity<Object> handleDuplicateEntityException(DuplicateEntity duplicateEntity) {
        Map<String, String> errors = new HashMap<>();
        errors.put("javaVersion", duplicateEntity.getMessage());

        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(errors).time(LocalDateTime.now()).build());
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> enumValidationException(InvalidFormatException exception) {
        Map<String, String> error = new HashMap<>();

        String errorDetails = "Invalid value: '" + exception.getValue() + "'. Valid values must be of type: " + exception.getTargetType().getTypeName() ;
        
        if (exception.getTargetType() != null && exception.getTargetType().isEnum()) {

            errorDetails = String.format("Invalid value: '%s' for the field: '%s'. Valid values are: %s.",
                    exception.getValue(), exception.getPath().getLast().getFieldName(), Arrays.toString(exception.getTargetType().getEnumConstants()));

            error.put(exception.getPath().getLast().getFieldName(), errorDetails);
        }

        error.put(exception.getPath().getLast().getFieldName(), errorDetails);
   

        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(error).time(LocalDateTime.now()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );


        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(errors).time(LocalDateTime.now()).build());
    }

    @ExceptionHandler(UnsupportedJavaVersion.class)
    public ResponseEntity<Object> handleUnsupportedJavaVersion(UnsupportedJavaVersion exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("javaVersion", exception.getMessage());

        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(errors).time(LocalDateTime.now()).build());
    }

    @ExceptionHandler(InvalidEntity.class)
    public ResponseEntity<Object> handleInvalidEntity(InvalidEntity exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("javaVersion", exception.getMessage());

        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(errors).time(LocalDateTime.now()).build());
    }
}
