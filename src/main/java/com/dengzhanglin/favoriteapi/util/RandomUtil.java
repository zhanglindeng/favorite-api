package com.dengzhanglin.favoriteapi.util;

import java.util.Random;

public class RandomUtil {
    public static String number(Integer length) {
        String sources = "0123456789";
        Random rand = new Random();
        final Integer maxIndex = sources.length() - 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(sources.charAt(rand.nextInt(maxIndex)));
        }
        return sb.toString();
    }
}
