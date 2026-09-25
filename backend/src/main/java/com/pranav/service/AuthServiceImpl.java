package com.pranav.service;

import com.pranav.dto.AppUserDTO;
import com.pranav.dto.AuthDTO;
import com.pranav.dto.AuthResponseDTO;
import com.pranav.model.AppUser;
import com.pranav.model.Role;
import com.pranav.utils.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public AuthResponseDTO registerUser(AppUserDTO appUserDTO) {
        if(userService.findByUsername(appUserDTO.getUsername()) != null){
            return new AuthResponseDTO(null, "Error : User already exists.");
        }

        AppUser appUser = new AppUser();
        appUser.setFullName(appUserDTO.getFullName());
        appUser.setUsername(appUserDTO.getUsername());
        appUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        appUser.setRole(Role.USER);
        userService.saveUser(appUser);

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername((appUser.getUsername()));
        authDTO.setPassword(appUserDTO.getPassword());

        return loginUser(authDTO);
    }

    @Override
    public AuthResponseDTO loginUser(AuthDTO authDTO) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authDTO.getUsername(),
                            authDTO.getPassword()
                    )
            );
            final String token = jwtUtil.generateToken(authDTO.getUsername());

            return new AuthResponseDTO(token, "SUCCESS");
        }catch (BadCredentialsException e){
            return new AuthResponseDTO(null, "Error : Invalid Username or Password");
        }
    }
}
