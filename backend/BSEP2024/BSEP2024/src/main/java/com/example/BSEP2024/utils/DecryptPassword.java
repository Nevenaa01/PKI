package com.example.BSEP2024.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class DecryptPassword {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    // Metoda za generiranje ključa proizvoljne duljine iz proizvoljno dugačkog unosa
    private static SecretKeySpec generateKey(String keyString) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(keyString.getBytes("UTF-8"));
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    // Metoda za dekripciju podataka
    public static String decrypt(String ciphertext, String key) throws Exception {
        SecretKeySpec secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes);
    }

    public static String encrypt(String plaintext, String key) throws Exception {
        SecretKeySpec secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
