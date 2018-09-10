package org.benetech.mathshare.mappers;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.model.entity.Scratchpad;

public final class MapperUtils {

    public static String toCode(Long shareCode) {
        return shareCode == null ? null : UrlCodeConverter.toUrlCode(shareCode);
    }

    public static Long fromCode(String editCode) {
        return editCode == null ? null : UrlCodeConverter.fromUrlCode(editCode);
    }

    public static String fromScratchpad(Scratchpad scratchpad) {
        return scratchpad == null ? null : scratchpad.getContent();
    }

    public static Scratchpad toScratchpad(String content) {
        return content == null ? null : new Scratchpad(content);
    }

    private MapperUtils() {

    }
}
