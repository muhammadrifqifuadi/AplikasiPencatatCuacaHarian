/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.weatherloggerapp;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 *
 * @author MSI GF63
 */
public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    // PERHATIAN: Kunci ini tidak aman. Dalam aplikasi nyata, gunakan cara yang lebih aman untuk mengelola kunci.
    private static final byte[] KEY = "MySuperSecretKey".getBytes(); 

    public static String encrypt(String valueToEnc) throws Exception {
        if (valueToEnc == null || valueToEnc.isEmpty()) {
            return "";
        }
        SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByteValue = cipher.doFinal(valueToEnc.getBytes("utf-8"));
        return Base64.getEncoder().encodeToString(encryptedByteValue);
    }

    public static String decrypt(String encryptedValue) throws Exception {
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            return "";
        }
        SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByteValue = Base64.getDecoder().decode(encryptedValue);
        byte[] originalByteValue = cipher.doFinal(decryptedByteValue);
        return new String(originalByteValue, "utf-8");
    }
}
