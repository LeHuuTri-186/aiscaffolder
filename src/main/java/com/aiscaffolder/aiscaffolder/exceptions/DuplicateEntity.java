package com.aiscaffolder.aiscaffolder.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicateEntity extends RuntimeException {
    public DuplicateEntity(String message) {
        super(message);
    }
}
