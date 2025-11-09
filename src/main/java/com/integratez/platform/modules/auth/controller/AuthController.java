package com.integratez.platform.modules.auth.controller;

import com.integratez.platform.modules.auth.dto.LoginRequest;
import com.integratez.platform.modules.auth.dto.RegisterRequest;
import com.integratez.platform.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {
        return authService.register( req.getEmail(),req.getUsername(), req.getPassword(), req.getRole());
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) {
        return authService.login(req.getUsername(), req.getPassword());
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        String message = authService.verifyUser(token);
        return ResponseEntity.ok(message);
    }

}


