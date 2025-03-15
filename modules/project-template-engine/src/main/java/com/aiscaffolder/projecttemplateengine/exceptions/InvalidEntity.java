package com.aiscaffolder.projecttemplateengine.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidEntity extends RuntimeException {
    public InvalidEntity(String message) {
        super(message);
    }
}
