package com.example.myapp3;

import java.security.SecureRandom;

public class RandomTextGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomText() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(generateRandomString(10)); // Генерация первой части текста
        stringBuilder.append(" – "); // Добавление разделителя
        stringBuilder.append(generateRandomString(10)); // Генерация второй части текста

        return stringBuilder.toString();
    }

    private static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        String randomText = RandomTextGenerator.generateRandomText();
        System.out.println(randomText);
    }
}
