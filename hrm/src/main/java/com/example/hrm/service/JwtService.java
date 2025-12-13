package com.example.hrm.service;

import com.example.hrm.entity.UserAccount;
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
    @Value("${app.jwt.signerKeyAccess}")
    protected String SECRET_KEY_ACCESS;
    @Value("${app.jwt.signerKeyRefresh}")
    protected String SECRET_KEY_REFRESH;
    @Value("${app.jwt.valid-duration}")
    protected long ACCESS_TOKEN_EXPIRATION;

    @Value("${app.jwt.refreshable-duration}")
    protected long REFRESH_TOKEN_EXPIRATION;

    private final RefreshTokenRepository refreshTokenRepository;

    public String generateAccessToken(UserAccount user) throws JOSEException {
        return generateToken(user, SECRET_KEY_ACCESS, "access", ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(UserAccount user) throws JOSEException {
        return generateToken(user, SECRET_KEY_REFRESH, "refresh", REFRESH_TOKEN_EXPIRATION);
    }

    private String generateToken(UserAccount user, String secret, String type, long expirationMs) throws JOSEException {
        JWTClaimsSet claims = buildClaims(user, type, expirationMs);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claims);

        signedJWT.sign(new MACSigner(secret.getBytes()));

        return signedJWT.serialize();
    }

    private JWTClaimsSet buildClaims(UserAccount user, String type, long expirationMs) {
        StringJoiner scopes = new StringJoiner(" ");
        user.getRoles().forEach(role -> scopes.add(role.getName()));

        return new JWTClaimsSet.Builder()
                .issueTime(new Date())
                .subject(user.getUsername())
                .claim("scope", scopes.toString())
                .claim("type", type)
                .issuer("myapp.com")
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(
                        new Date(
                                Instant.now().plus(expirationMs, ChronoUnit.SECONDS)
                                        .toEpochMilli()
                        )
                )
                .build();
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

    public SignedJWT getClaims(String token) throws ParseException {
        return SignedJWT.parse(token);
    }

    public void verifyRefreshToken(String token) throws ParseException, JOSEException {

        SignedJWT jwt = SignedJWT.parse(token);

        // 1️⃣ Verify signature trước
        if (!jwt.verify(new MACVerifier(SECRET_KEY_REFRESH.getBytes()))) {
            throw new AppException(ErrorCode.INVALID_TOKEN, 401);
        }

        String jwtId = getJwtId(jwt);
        if (jwtId == null) {
            throw new AppException(ErrorCode.INVALID_TOKEN, 401);
        }

        refreshTokenRepository.findByJwtID(jwtId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN, 401));
    }

    private static String getJwtId(SignedJWT jwt) throws ParseException {
        var claims = jwt.getJWTClaimsSet();

        // 2️⃣ Check type
        String type = claims.getStringClaim("type");
        if (!"refresh".equals(type)) {
            throw new AppException(ErrorCode.INVALID_TOKEN_TYPE, 401);
        }

        // 3️⃣ Check expiration
        Date expirationTime = claims.getExpirationTime();
        if (expirationTime == null || expirationTime.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_HAS_EXPIRED, 401);
        }

        // 4️⃣ Check jti
        String jwtId = claims.getJWTID();
        return jwtId;
    }

}
