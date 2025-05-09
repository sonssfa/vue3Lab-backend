// Aes256KeyProvider.java
package kr.sonss.lab.backend.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class Aes256KeyProvider {

    @Value("${aes256.secret}")
    private String base64Key;

    public SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, "AES");
    }
}