package com.aiscaffolder.aiscaffolder.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiExceptionResponse {

    private String message;

    private HttpStatus status;

    private LocalDateTime time;

}
