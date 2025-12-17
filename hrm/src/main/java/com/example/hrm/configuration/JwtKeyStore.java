package com.example.hrm.configuration;

import com.example.hrm.enums.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtKeyStore {

    private final Map<String, byte[]> keys;

    public JwtKeyStore(
            @Value("${app.jwt.signerKeyAccess}") String access,
            @Value("${app.jwt.signerKeyRefresh}") String refresh,
            @Value("${app.jwt.signerKeyActivation}") String activation
    ) {
        keys = Map.of(
                TokenType.ACCESS.name(), access.getBytes(),
                TokenType.REFRESH.name(), refresh.getBytes(),
                TokenType.ACTIVATION.name(), activation.getBytes()
        );
    }

    public byte[] getKey(String kid) {
        return keys.get(kid);
    }
}
