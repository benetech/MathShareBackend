package org.benetech.mathshare.mappers;

import org.benetech.mathshare.converters.UrlCodeConverter;

public final class MapperUtils {

    public static String toCode(Long shareCode) {
        return shareCode == null ? null : UrlCodeConverter.toUrlCode(shareCode);
    }

    public static Long fromCode(String editCode) {
        return editCode == null ? null : UrlCodeConverter.fromUrlCode(editCode);
    }

    private MapperUtils() {

    }
}
