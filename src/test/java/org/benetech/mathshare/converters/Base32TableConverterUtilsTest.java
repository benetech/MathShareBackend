package org.benetech.mathshare.converters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class Base32TableConverterUtilsTest {

    @Test
    public void toSignMethodShouldConvert0To0() {
        Assert.assertEquals('0', Base32TableConverterUtils.toSign(0));
    }

    @Test
    public void toSignMethodShouldConvert1To1() {
        Assert.assertEquals('1', Base32TableConverterUtils.toSign(1));
    }

    @Test
    public void toSignMethodShouldConvert9To9() {
        Assert.assertEquals('9', Base32TableConverterUtils.toSign(9));
    }

    @Test
    public void toSignMethodShouldConvert10ToA() {
        Assert.assertEquals('A', Base32TableConverterUtils.toSign(10));
    }

    @Test
    public void toSignMethodShouldConvert31ToV() {
        Assert.assertEquals('V', Base32TableConverterUtils.toSign(31));
    }

    @Test
    public void fromSignMethodShouldConvert0To0() {
        Assert.assertEquals(0, Base32TableConverterUtils.fromSign('0'));
    }

    @Test
    public void fromSignMethodShouldConvert1To1() {
        Assert.assertEquals(1, Base32TableConverterUtils.fromSign('1'));
    }

    @Test
    public void fromSignMethodShouldConvert9To9() {
        Assert.assertEquals(9, Base32TableConverterUtils.fromSign('9'));
    }

    @Test
    public void fromSignMethodShouldConvertATo10() {
        Assert.assertEquals(10, Base32TableConverterUtils.fromSign('A'));
    }

    @Test
    public void fromSignMethodShouldConvertVTo31() {
        Assert.assertEquals(31, Base32TableConverterUtils.fromSign('V'));
    }

    @Test
    public void fromSignMethodShouldConvertLowercaseATo10() {
        Assert.assertEquals(10, Base32TableConverterUtils.fromSign('a'));
    }

    @Test
    public void fromSignMethodShouldConvertLowercaseVTo31() {
        Assert.assertEquals(31, Base32TableConverterUtils.fromSign('v'));
    }
}
