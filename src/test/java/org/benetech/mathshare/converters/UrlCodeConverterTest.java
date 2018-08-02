package org.benetech.mathshare.converters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UrlCodeConverterTest {

    @Test(expected = IllegalArgumentException.class)
    public void fromUrlCodeMethodShouldThrowIllegalArgumentExceptionForNotBase32AndNotXCharacters() {
        UrlCodeConverter.fromUrlCode("123#eStrS");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromUrlCodeMethodShouldThrowIllegalArgumentExceptionForTooLongString() {
        UrlCodeConverter.fromUrlCode("800000000000000");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromUrlCodeMethodShouldThrowIllegalArgumentExceptionForEmptyString() {
        UrlCodeConverter.fromUrlCode("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromUrlCodeMethodShouldThrowIllegalArgumentExceptionForNull() {
        UrlCodeConverter.fromUrlCode(null);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToNotBoundaryLongValueWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("PF5LVV");
        Assert.assertEquals(854775807, result);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToMaxLongValueWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("7VVVVVVVVVVVV");
        Assert.assertEquals(Long.MAX_VALUE, result);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToAlmostMaxLongValueWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("7VVVVVVVVVVVU");
        Assert.assertEquals(Long.MAX_VALUE - 1, result);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToMinLongValueWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("X8000000000000");
        Assert.assertEquals(Long.MIN_VALUE, result);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToAlmostMinLongValueWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("X7VVVVVVVVVVVV");
        Assert.assertEquals(Long.MIN_VALUE + 1, result);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToZeroWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("0");
        Assert.assertEquals(0L, result);
    }

    @Test
    public void fromUrlCodeMethodShouldTreatXAsMinus() {
        long result = UrlCodeConverter.fromUrlCode("XFV30M30H");
        Assert.assertEquals(-548783459345L, result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertMaxLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(Long.MAX_VALUE);
        Assert.assertEquals("7VVVVVVVVVVVV", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertAlmostMaxLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(Long.MAX_VALUE - 1);
        Assert.assertEquals("7VVVVVVVVVVVU", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertMinLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(Long.MIN_VALUE);
        Assert.assertEquals("X8000000000000", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertAlmostMinLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(Long.MIN_VALUE + 1);
        Assert.assertEquals("X7VVVVVVVVVVVV", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertZeroToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(0L);
        Assert.assertEquals("0", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertNotBoundaryLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(542436435L);
        Assert.assertEquals("G59R2J", result);
    }

    @Test
    public void toUrlCodeMethodShouldTreatMinusAsX() {
        String result = UrlCodeConverter.toUrlCode(-5435345342L);
        Assert.assertEquals("X51VHIDU", result);
    }
}
