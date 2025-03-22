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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestControllerAdvice(basePackageClasses = ProjectGenerationController.class)
public class ProjectGenerationControllerAdvice {
    final ObjectMapper objectMapper;

    public ProjectGenerationControllerAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(DuplicateEntity.class)
    public ResponseEntity<Object> handleDuplicateEntityException(DuplicateEntity duplicateEntity) {
        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(duplicateEntity.getMessage()).time(LocalDateTime.now()).build());
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> enumValidationException(InvalidFormatException exception) {
        String errorDetails = "Invalid value: '" + exception.getValue() + "'. Valid values must be of type: " + exception.getTargetType().getSimpleName();

        if (exception.getTargetType() != null && exception.getTargetType().isEnum()) {

            errorDetails = String.format("Invalid value: '%s' for the field: '%s'. Valid values are: %s.",
                    exception.getValue(), exception.getPath().getLast().getFieldName(), Arrays.toString(exception.getTargetType().getEnumConstants()));
        }
        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(errorDetails).time(LocalDateTime.now()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String error = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();

        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(error).time(LocalDateTime.now()).build());
    }

    @ExceptionHandler(UnsupportedJavaVersion.class)
    public ResponseEntity<Object> handleUnsupportedJavaVersion(UnsupportedJavaVersion exception) {
        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(exception.getMessage()).time(LocalDateTime.now()).build());
    }

    @ExceptionHandler(InvalidEntity.class)
    public ResponseEntity<Object> handleInvalidEntity(InvalidEntity exception) {
        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(exception.getMessage()).time(LocalDateTime.now()).build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex)  {
        String message = ex.getMessage();

        Pattern enumPattern = Pattern.compile("Cannot deserialize value of type `[^`]+` from String \"([^\"]+)\".*?accepted for Enum class: \\[(.*?)]");
        Matcher enumMatcher = enumPattern.matcher(message);

        if (enumMatcher.find()) {
            String invalidValue = enumMatcher.group(1);  // Extracts the invalid value
            String allowedValues = enumMatcher.group(2);  // Extracts allowed values

            String error = String.format(
                    "Invalid value '%s'. Allowed values are: [%s].",
                    invalidValue, allowedValues
            );

            return ResponseEntity.badRequest().body(ApiExceptionResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(error)
                    .time(LocalDateTime.now())
                    .build());
        }

        return ResponseEntity.badRequest().body(ApiExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Unrecognized field '" + message.split("\"")[1] + "'")
                .time(LocalDateTime.now())
                .build());
    }
}
