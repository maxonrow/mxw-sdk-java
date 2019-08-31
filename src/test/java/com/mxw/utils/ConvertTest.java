package com.mxw.utils;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class ConvertTest {

    @Test
    public void testConvertToCIN() {
        assertToCIN(Convert.Unit.MXW, "1", "1000000000000000000");
        assertToCIN(Convert.Unit.JCIN, "1", "1000000000000000");
        assertToCIN(Convert.Unit.TCIN, "1", "1000000000000");
        assertToCIN(Convert.Unit.GCIN, "1", "1000000000");
        assertToCIN(Convert.Unit.MCIN, "1", "1000000");
        assertToCIN(Convert.Unit.KCIN, "1", "1000");
        assertToCIN(Convert.Unit.CIN, "1", "1");
    }

    private void assertToCIN(Convert.Unit unit, String amount, String compare) {
        Assert.assertEquals(Convert.toCIN(amount, unit), new BigDecimal(compare));
    }

    @Test
    public void testConvertFromCIN() {
        assertFromCIN(Convert.Unit.MXW, "1000000000000000000", "1");
        assertFromCIN(Convert.Unit.JCIN, "1000000000000000", "1");
        assertFromCIN(Convert.Unit.TCIN, "1000000000000", "1");
        assertFromCIN(Convert.Unit.GCIN, "1000000000", "1");
        assertFromCIN(Convert.Unit.MCIN, "1000000", "1");
        assertFromCIN(Convert.Unit.KCIN, "1000", "1");
        assertFromCIN(Convert.Unit.CIN, "1", "1");
    }

    private void assertFromCIN(Convert.Unit unit, String amount, String compare) {
        Assert.assertEquals(Convert.fromCIN(amount, unit), new BigDecimal(compare));
    }


}
