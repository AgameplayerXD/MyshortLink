package com.xwj.shortlink.util;

import java.security.SecureRandom;

/**
 * 短链接分组 ID 随机生成器
 */
public class RandomGenerator {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成英文加数字的 6 位组合
     * @return
     */
    public static String generateRandom() {
        return generateRandom(6);
    }

    /**
     * 生成指定位数的英文加数字组合
     * @param length
     * @return
     */
    public static String generateRandom(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
