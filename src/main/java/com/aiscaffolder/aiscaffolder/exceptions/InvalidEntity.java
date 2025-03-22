package com.aiscaffolder.aiscaffolder.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidEntity extends RuntimeException {
    public InvalidEntity(String message) {
        super(message);
    }
}
