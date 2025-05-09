package kr.sonss.lab.backend.common.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class Aes256Util {

    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;

    // 암호화: IV + 암호문 → Base64
    public String encrypt(String plainText, SecretKey secretKey) throws Exception {
        byte[] iv = generateIv();
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(AES_CBC);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedWithIv = new byte[IV_SIZE + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, IV_SIZE);
        System.arraycopy(encrypted, 0, encryptedWithIv, IV_SIZE, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    // 복호화: Base64 → IV + 암호문 분리
    public String decrypt(String encryptedTextWithIv, SecretKey secretKey) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedTextWithIv);

        byte[] iv = new byte[IV_SIZE];
        byte[] encryptedBytes = new byte[decoded.length - IV_SIZE];
        System.arraycopy(decoded, 0, iv, 0, IV_SIZE);
        System.arraycopy(decoded, IV_SIZE, encryptedBytes, 0, encryptedBytes.length);

        Cipher cipher = Cipher.getInstance(AES_CBC);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // IV 생성
    private byte[] generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
