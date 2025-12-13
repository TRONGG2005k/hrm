package com.example.hrm.service;

import com.example.hrm.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${app.jwt.signerKeyAccess}")
    protected String SECRET_KEY_ACCESS;
    @Value("${app.jwt.signerKeyRefresh}")
    protected String SECRET_KEY_REFRESH;
    @Value("${app.jwt.valid-duration}")
    protected long ACCESS_TOKEN_EXPIRATION;

    @Value("${app.jwt.refreshable-duration}")
    protected long REFRESH_TOKEN_EXPIRATION;

    private String generateToken(UserAccount user, String secret, String type, long expirationMs) throws JOSEException {
        JWTClaimsSet claims = buildClaims(user, type, expirationMs);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claims);

        signedJWT.sign(new MACSigner(secret.getBytes()));

        return signedJWT.serialize();
    }


}
