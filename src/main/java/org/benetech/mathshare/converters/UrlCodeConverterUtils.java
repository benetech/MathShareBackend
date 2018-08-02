package org.benetech.mathshare.converters;

import java.util.Locale;

public final class UrlCodeConverterUtils {

    private static final int BASE = 32;
    private static final String MINUS_SIGN = "X";
    private static final String MIN_LONG_CODE = MINUS_SIGN + "8000000000000";

    public static String toUrlCode(Long number) {
        validateNumber(number);
        if (number.equals(Long.MIN_VALUE)) {
            return MIN_LONG_CODE;
        } else if (number.equals(0L)) {
            return "0";
        } else {
            var code = convertNumberToCode(Math.abs(number));
            return number > 0 ? code : MINUS_SIGN.concat(code);
        }
    }

    public static long fromUrlCode(String code) {
        validateCode(code);
        if (code.equals(MIN_LONG_CODE)) {
            return Long.MIN_VALUE;
        }
        return convertCodeToNumber(adjustTheCode(code), isNumberPositive(code));
    }

    private static void validateCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code can't be null");
        } else if (code.isEmpty()) {
            throw new IllegalArgumentException("Code can't be empty");
        } else if (code.length() > MIN_LONG_CODE.length()) {
            throw new IllegalArgumentException("Code is too long");
        } else if (!code.matches("[a-xA-X0-9]*")) {
            throw new IllegalArgumentException("Code is not valid");
        }
    }

    private static void validateNumber(Long number) {
        if (number == null) {
            throw new IllegalArgumentException("Number can't be null");
        }
    }

    private static boolean isNumberPositive(String code) {
        return !code.contains(String.valueOf(MINUS_SIGN));
    }

    private static String adjustTheCode(String code) {
        return code.replaceFirst(MINUS_SIGN, "").toUpperCase(Locale.ROOT);
    }

    private static long convertCodeToNumber(String code, boolean positive) {
        var result = 0L;
        var chars = code.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            result += countBasePow(i) * Base32TableConverterUtils.fromSign(chars[chars.length - 1 - i]);
        }
        return positive ? result : (-1) * result;
    }

    private static String convertNumberToCode(long value) {
        var result = "";
        var number = value;
        while (number > 0) {
            var remainder = number % BASE;
            number = number - remainder;
            number /= BASE;
            result = String.valueOf(Base32TableConverterUtils.toSign(remainder)).concat(result);
        }
        return result;
    }

    private static long countBasePow(int value) {
        long result = 1;
        long sq = BASE;
        long power = value;
        while (power > 0) {
            if (power % 2 == 1) {
                result *= sq;
            }
            sq = sq * sq;
            power /= 2;
        }
        return result;
    }

    private UrlCodeConverterUtils() { }
}
