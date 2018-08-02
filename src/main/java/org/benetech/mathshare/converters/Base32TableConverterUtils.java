package org.benetech.mathshare.converters;

import java.util.Locale;

public final class Base32TableConverterUtils {

    private static final int NUMBER_OF_DIGITS = 10;

    public static char toSign(long number) {
        if (number < NUMBER_OF_DIGITS) {
            return (char) ('0' + number);
        } else {
            return (char) ('A' + number - NUMBER_OF_DIGITS);
        }
    }

    public static long fromSign(char sign) {
        var upperCaseSign = String.valueOf(sign).toUpperCase(Locale.ROOT).charAt(0);

        if (upperCaseSign <= '9') {
            return upperCaseSign - '0';
        } else {
            return upperCaseSign - 'A' + NUMBER_OF_DIGITS;
        }
    }

    private Base32TableConverterUtils() { }
}
