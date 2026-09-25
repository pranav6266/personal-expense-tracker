package com.pranav.service;

import com.pranav.dto.AppUserDTO;
import com.pranav.dto.AuthDTO;
import com.pranav.dto.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO registerUser(AppUserDTO appUserDTO);
    AuthResponseDTO loginUser(AuthDTO authDTO);
}
