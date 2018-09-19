package org.benetech.mathshare.converters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UrlCodeConverterTest {

    @Test(expected = IllegalArgumentException.class)
    public void fromUrlCodeMethodShouldThrowIllegalArgumentExceptionForNotBase32Characters() {
        UrlCodeConverter.fromUrlCode("123#eStrS");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromUrlCodeMethodShouldThrowIllegalArgumentExceptionForTooLongString() {
        UrlCodeConverter.fromUrlCode("P777777777776666666");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromUrlCodeMethodShouldThrowIllegalArgumentExceptionForTooShortString() {
        UrlCodeConverter.fromUrlCode("E4AM5WE4AM5W");
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
        long result = UrlCodeConverter.fromUrlCode("AAAAAABAKTWFG");
        Assert.assertEquals(542436435L, result);
    }
    @Test
    public void fromUrlCodeMethodShouldConvertCodeToNotBoundaryMinusLongValueWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("777777V4A43EE");
        Assert.assertEquals(-5435345342L, result);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToMaxLongValueWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("P777777777776");
        Assert.assertEquals(Long.MAX_VALUE, result);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToAlmostMaxLongValueWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("P777777777774");
        Assert.assertEquals(Long.MAX_VALUE - 1, result);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToMinLongValueWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("QAAAAAAAAAAAA");
        Assert.assertEquals(Long.MIN_VALUE, result);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToAlmostMinLongValueWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("QAAAAAAAAAAAC");
        Assert.assertEquals(Long.MIN_VALUE + 1, result);
    }

    @Test
    public void fromUrlCodeMethodShouldConvertCodeToZeroWithBase32() {
        long result = UrlCodeConverter.fromUrlCode("AAAAAAAAAAAAA");
        Assert.assertEquals(0L, result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertMaxLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(Long.MAX_VALUE);
        Assert.assertEquals("P777777777776", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertAlmostMaxLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(Long.MAX_VALUE - 1);
        Assert.assertEquals("P777777777774", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertMinLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(Long.MIN_VALUE);
        Assert.assertEquals("QAAAAAAAAAAAA", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertAlmostMinLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(Long.MIN_VALUE + 1);
        Assert.assertEquals("QAAAAAAAAAAAC", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertZeroToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(0L);
        Assert.assertEquals("AAAAAAAAAAAAA", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertNotBoundaryLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(542436435L);
        Assert.assertEquals("AAAAAABAKTWFG", result);
    }

    @Test
    public void toUrlCodeMethodShouldConvertNotBoundaryMinusLongValueToCodeWithBase32() {
        String result = UrlCodeConverter.toUrlCode(-5435345342L);
        Assert.assertEquals("777777V4A43EE", result);
    }
}
