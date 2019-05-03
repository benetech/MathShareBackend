package org.benetech.mathshare.mappers;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.model.entity.Scratchpad;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;

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

    public static Long nextCode(EntityManager entityManager) {
        String queryStr = "select generateBigInt(nextval('url_sequence')\\:\\:bigint) as code";
        try {
            Query query = entityManager.createNativeQuery(queryStr);
            Object result = query.getSingleResult();
            return ((BigInteger) result).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    private MapperUtils() {

    }
}
