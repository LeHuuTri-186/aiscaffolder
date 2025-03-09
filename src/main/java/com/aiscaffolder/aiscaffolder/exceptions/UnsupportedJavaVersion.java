package com.aiscaffolder.aiscaffolder.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UnsupportedJavaVersion extends RuntimeException {
    public UnsupportedJavaVersion(String message) {}
}
