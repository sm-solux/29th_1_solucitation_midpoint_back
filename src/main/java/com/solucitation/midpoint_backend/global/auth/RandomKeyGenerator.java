package com.solucitation.midpoint_backend.global.auth;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 256비트 랜덤 키 생성기 - JWT 서명 키 생성에 사용
 */
public class RandomKeyGenerator {

    public static void main(String[] args) {
        byte[] key = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);

        String base64Key = Base64.getEncoder().encodeToString(key);
        System.out.println("Generated 256-bit random key: " + base64Key);
    }
}
