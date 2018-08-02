package org.benetech.mathshare.converters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class Base32TableConverterTest {

    @Test
    public void toSignMethodShouldConvert0To0() {
        Assert.assertEquals('0', Base32TableConverter.toSign(0));
    }

    @Test
    public void toSignMethodShouldConvert1To1() {
        Assert.assertEquals('1', Base32TableConverter.toSign(1));
    }

    @Test
    public void toSignMethodShouldConvert9To9() {
        Assert.assertEquals('9', Base32TableConverter.toSign(9));
    }

    @Test
    public void toSignMethodShouldConvert10ToA() {
        Assert.assertEquals('A', Base32TableConverter.toSign(10));
    }

    @Test
    public void toSignMethodShouldConvert31ToV() {
        Assert.assertEquals('V', Base32TableConverter.toSign(31));
    }

    @Test
    public void fromSignMethodShouldConvert0To0() {
        Assert.assertEquals(0, Base32TableConverter.fromSign('0'));
    }

    @Test
    public void fromSignMethodShouldConvert1To1() {
        Assert.assertEquals(1, Base32TableConverter.fromSign('1'));
    }

    @Test
    public void fromSignMethodShouldConvert9To9() {
        Assert.assertEquals(9, Base32TableConverter.fromSign('9'));
    }

    @Test
    public void fromSignMethodShouldConvertATo10() {
        Assert.assertEquals(10, Base32TableConverter.fromSign('A'));
    }

    @Test
    public void fromSignMethodShouldConvertVTo31() {
        Assert.assertEquals(31, Base32TableConverter.fromSign('V'));
    }

    @Test
    public void fromSignMethodShouldConvertLowercaseATo10() {
        Assert.assertEquals(10, Base32TableConverter.fromSign('a'));
    }

    @Test
    public void fromSignMethodShouldConvertLowercaseVTo31() {
        Assert.assertEquals(31, Base32TableConverter.fromSign('v'));
    }
}
