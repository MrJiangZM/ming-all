package com.ming.blog.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RSADemo {

    public static void main(String[] args) throws NoSuchAlgorithmException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        byte[] publicBytes = pair.getPublic().getEncoded();
        byte[] privateBytes = pair.getPrivate().getEncoded();

        System.out.println("public key: " + base64Encode(publicBytes));

        System.out.println("=====================================");

        System.out.println("private key: " + base64Encode(privateBytes));
    }

    static String base64Encode(byte[] bytes) {

        return Base64.getEncoder().encodeToString(bytes);
    }
}