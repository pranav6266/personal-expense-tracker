package com.pranav.expensetrackerui.models;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String message;
}