package com.example.hrm.controller;

import com.example.hrm.dto.request.LoginRequest;
import com.example.hrm.dto.response.LoginResponse;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.service.AuthService;
import com.example.hrm.service.JwtService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("${app.api-prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) throws ParseException, JOSEException {
        var response = authService.login(request);

        var cookie = jwtService.createRefreshCookie(response.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(LoginResponse.builder()
                        .accessToken(response.getAccessToken())
                        .roles(response.getRoles())
                        .build());
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) throws ParseException {
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token not found.");
        }
        authService.logout(refreshToken);
        // Xoá cookie trên trình duyệt
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // nếu bạn dùng HTTPS
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logout success");
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ){
        try {
            var response = authService.refreshToken(refreshToken);
            var cookie = jwtService.createRefreshCookie(response.getRefreshToken());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(LoginResponse.builder()
                            .accessToken(response.getAccessToken())
                            .roles(response.getRoles())
                            .build());
        }  catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN, 409);
        }
    }
}
