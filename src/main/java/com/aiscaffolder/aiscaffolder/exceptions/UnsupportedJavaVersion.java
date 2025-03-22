package com.aiscaffolder.aiscaffolder.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnsupportedJavaVersion extends RuntimeException {
    public UnsupportedJavaVersion(String message) {}
}
