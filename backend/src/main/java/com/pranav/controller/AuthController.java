package com.pranav.controller;

import com.pranav.dto.AppUserDTO;
import com.pranav.dto.AuthDTO;
import com.pranav.dto.AuthResponseDTO;
import com.pranav.model.AppUser;
import com.pranav.service.AuthService;
import com.pranav.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }


    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody AppUserDTO appUserDTO){
       AuthResponseDTO response = authService.registerUser(appUserDTO);
       if("SUCCESS".equals(response.getMessage())){
           return ResponseEntity.ok(response);
       }else {
           return ResponseEntity.badRequest().body(response);
       }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthDTO authDTO){
        AuthResponseDTO response = authService.loginUser(authDTO);
        if("SUCCESS".equals(response.getMessage())){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/auth/validate")
    public ResponseEntity<?> validateToken(org.springframework.security.core.Authentication authentication){
        if(authentication == null || !authentication.isAuthenticated()){
            return ResponseEntity.status(401).body(java.util.Map.of("message","Unauthorized"));
        }
        String username = authentication.getName();
        com.pranav.model.AppUser user = userService.findByUsername(username);
        return ResponseEntity.ok(java.util.Map.of(
                "username", username,
                "role", user.getRole().name()
        ));
    }
}
