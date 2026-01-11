package com.sneakyzxje.libs.security.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sneakyzxje.libs.security.dto.RefreshTokenRequest;
import com.sneakyzxje.libs.security.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/easy-auth")
public class AuthController {
    
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        String newAccessToken = refreshTokenService.processRefresh(request.refreshToken());
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}
