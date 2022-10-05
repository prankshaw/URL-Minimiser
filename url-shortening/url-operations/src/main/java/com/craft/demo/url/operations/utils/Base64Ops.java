package com.craft.demo.url.operations.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class Base64Ops {

    private static final char[] BASE64_CODE = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789-_".toCharArray();
    private static final HashMap<Character, Integer> BASE64_INDEX = new HashMap<>();

    static {
        for (int i = 0; i < BASE64_CODE.length; i++) {
            BASE64_INDEX.put(BASE64_CODE[i], i);
        }
    }

    public static String encode(long num) {
        StringBuilder encodedText = new StringBuilder();

        do {
            int remainder = (int) (num % 64);
            encodedText.append(BASE64_CODE[remainder]);
            num /= 64;
        } while (num > 0);

        return encodedText.toString();
    }

    public static long decode(String hashedCode) {
        long decodedNumber = 0;
        long pow = 1;

        for (int i = 0; i < hashedCode.length(); i++) {
            int digit = BASE64_INDEX.get(hashedCode.charAt(i));
            decodedNumber += digit * pow;
            pow *= 64;
        }

        return decodedNumber;
    }
}
