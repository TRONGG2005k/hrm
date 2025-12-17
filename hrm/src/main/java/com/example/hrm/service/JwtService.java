package com.example.hrm.service;

import com.example.hrm.configuration.JwtKeyStore;
import com.example.hrm.entity.UserAccount;
import com.example.hrm.enums.TokenType;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.repository.RefreshTokenRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtKeyStore keyStore;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.valid-duration}")
    private long accessExp;

    @Value("${app.jwt.refreshable-duration}")
    private long refreshExp;

    private static final String ISSUER = "myapp.com";

    /* ================= GENERATE ================= */

    public String generateAccessToken(UserAccount user) throws JOSEException {
        return generateToken(user, TokenType.ACCESS, accessExp);
    }

    public String generateRefreshToken(UserAccount user) throws JOSEException {
        return generateToken(user, TokenType.REFRESH, refreshExp);
    }

    public ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // true nếu HTTPS
                .path("/")
                .maxAge(30 * 24 * 60 * 60) // 7 ngày
                .sameSite("Lax")
                .build();
    }

    public String generateActivationToken(UserAccount user) throws JOSEException {
        return generateToken(user, TokenType.ACTIVATION, 900);
    }

    private String generateToken(UserAccount user, TokenType type, long expSeconds)
            throws JOSEException {

        JWTClaimsSet claims = buildClaims(user, type, expSeconds);

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS512)
                        .keyID(type.name()) // 🔥 KID
                        .build(),
                claims
        );

        jwt.sign(new MACSigner(keyStore.getKey(type.name())));
        return jwt.serialize();
    }

    private JWTClaimsSet buildClaims(UserAccount user, TokenType type, long expSeconds) {

        String scope = user.getRoles().stream()
                .map(r -> r.getName())
                .reduce((a, b) -> a + " " + b)
                .orElse("");

        return new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(ISSUER)
                .claim("scope", scope)
                .claim("type", type.name())
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(
                        Date.from(Instant.now().plus(expSeconds, ChronoUnit.SECONDS))
                )
                .build();
    }

    /* ================= VERIFY (CRYPTO) ================= */

    public JWTClaimsSet verifyAndParse(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            String kid = jwt.getHeader().getKeyID();
            byte[] key = keyStore.getKey(kid);

            if (key == null || !jwt.verify(new MACVerifier(key)))
                throw new AppException(ErrorCode.INVALID_TOKEN, 401);

            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            if (!ISSUER.equals(claims.getIssuer()))
                throw new AppException(ErrorCode.INVALID_TOKEN, 401);

            if (claims.getExpirationTime().before(new Date()))
                throw new AppException(ErrorCode.TOKEN_HAS_EXPIRED, 401);

            return claims;

        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_TOKEN, 401);
        }
    }

    /* ================= ASSERT PURPOSE ================= */

    public void assertAccessToken(JWTClaimsSet claims) throws ParseException {
        assertType(claims, TokenType.ACCESS);
    }

    public void assertRefreshToken(JWTClaimsSet claims) throws ParseException {
        assertType(claims, TokenType.REFRESH);

        refreshTokenRepository.findByJwtID(claims.getJWTID())
                .orElseThrow(() ->
                        new AppException(ErrorCode.INVALID_TOKEN, 401)
                );
    }

    public void assertActivationToken(JWTClaimsSet claims) throws ParseException {
        assertType(claims, TokenType.ACTIVATION);
    }

    void assertType(JWTClaimsSet claims, TokenType expected) throws ParseException {
        TokenType actual = TokenType.valueOf(
                claims.getStringClaim("type")
        );

        if (actual != expected)
            throw new AppException(ErrorCode.INVALID_TOKEN_TYPE, 401);
    }
}

