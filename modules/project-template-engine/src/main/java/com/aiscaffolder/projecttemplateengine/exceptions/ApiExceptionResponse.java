package com.aiscaffolder.projecttemplateengine.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiExceptionResponse {

    private Map<String, String> message;

    private HttpStatus status;

    private LocalDateTime time;

}
