package com.aiscaffolder.projecttemplateengine.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicateEntity extends RuntimeException {
    public DuplicateEntity(String message) {
        super(message);
    }
}
