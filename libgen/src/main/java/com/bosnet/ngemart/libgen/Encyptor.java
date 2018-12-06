package com.bosnet.ngemart.libgen;

import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

/**
 * Created by luis on 12/19/2016.
 * Purpose :
 */

public class Encyptor {

    private static final String RSA_DEFAULT_EXPONENT = "AQAB";

    public static String GetMD5Hash(String textToHash) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(textToHash.getBytes());
        byte messageDigest[] = digest.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : messageDigest)
            hexString.append(Integer.toHexString(0xFF & aMessageDigest));
        return hexString.toString();
    }

    public static String GetSHA256Hash(String textToHash) throws Exception{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] byteOfTextToHash=textToHash.getBytes("UTF-8");
        byte[] hashedByteArray = digest.digest(byteOfTextToHash);
        String encoded = Base64.encodeToString(hashedByteArray,Base64.DEFAULT);

        return encoded;
    }

    public static String EncryptRSA(String plainText, String strBase64Modulus) throws Exception {
        byte[] expBytes = Base64.decode(RSA_DEFAULT_EXPONENT, Base64.NO_WRAP);
        byte[] modBytes = Base64.decode(strBase64Modulus, Base64.NO_WRAP);

        BigInteger modulus = new BigInteger(1, modBytes);
        BigInteger exponent = new BigInteger(1, expBytes);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PublicKey publicKey = fact.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
    }
}
