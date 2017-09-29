package ru.simplex_software.smeta.excel;


import org.junit.Test;

import java.text.DecimalFormat;

public class ReportCreatorTest {

    @Test
    public void checkDecimalType() {
        DecimalFormat df = new DecimalFormat("###,###.###");
        System.out.println("df.format() = " + df.format(Math.PI*100000000000000l));
    }

}