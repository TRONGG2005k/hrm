package com.example.hrm.service;

import com.example.hrm.dto.request.LoginRequest;
import com.example.hrm.dto.response.LoginResponse;
import com.example.hrm.enums.TokenType;
import com.example.hrm.enums.UserStatus;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.redis.RefreshToken;
import com.example.hrm.repository.RefreshTokenRepository;
import com.example.hrm.repository.UserAccountRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * LOGIN
     */
    public LoginResponse login(LoginRequest request)
            throws JOSEException, ParseException {

        var user = userAccountRepository
                .findByUsernameAndIsDeletedFalse(request.getUsername())
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.INVALID_USERNAME_OR_PASSWORD, 401
                        )
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AppException(ErrorCode.USER_NOT_ACTIVE, 409);
        }

        if (user.getPassword() == null ||
                !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(
                    ErrorCode.INVALID_USERNAME_OR_PASSWORD, 401
            );
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        JWTClaimsSet refreshClaims = jwtService.verifyAndParse(refreshToken);
        jwtService.assertType(refreshClaims, TokenType.REFRESH);

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .jwtID(refreshClaims.getJWTID())
                        .username(user.getUsername())
                        .ttl(2_592_000L) // 30 days
                        .build()
        );

        String roles = jwtService
                .verifyAndParse(accessToken)
                .getStringClaim("scope");

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(roles)
                .build();
    }

    /**
     * LOGOUT
     */
    public void logout(String refreshToken)
            throws ParseException {

        JWTClaimsSet claims = jwtService.verifyAndParse(refreshToken);

        jwtService.assertRefreshToken(claims);

        String jwtId = claims.getJWTID();
        String username = claims.getSubject();

        userAccountRepository
                .findByUsernameAndIsDeletedFalseAndStatus(username, UserStatus.ACTIVE)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND, 404)
                );

        refreshTokenRepository.deleteById(jwtId);
    }

    /*
    * refresh
    * */
    public LoginResponse refreshToken(String refreshToken)
            throws JOSEException, ParseException {

        var claims = jwtService.verifyAndParse(refreshToken);

        jwtService.assertRefreshToken(claims);

        String username = claims.getSubject();

        refreshTokenRepository.deleteById(claims.getJWTID());

        var user = userAccountRepository
                .findByUsernameAndIsDeletedFalseAndStatus(username, UserStatus.ACTIVE)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND, 404)
                );

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        var claim = jwtService.verifyAndParse(newAccessToken);
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .jwtID(claim.getJWTID())
                        .username(user.getUsername())
                        .ttl(2_592_000L) // 30 days
                        .build()
        );


        return LoginResponse.builder()
                .refreshToken(newRefreshToken)
                .accessToken(newAccessToken)
                .build();
    }

}
