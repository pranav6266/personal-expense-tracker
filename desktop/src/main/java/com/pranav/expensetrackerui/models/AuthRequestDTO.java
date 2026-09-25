package com.pranav.expensetrackerui.models;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String fullName; // Required only for signup
    private String username;
    private String password;
}
