package com.codeborne.selenide.logevents;

import org.junit.Test;

public class SimpleReportTest {

    @Test
    public void reportShouldNotThrowNpe() {
        new SimpleReport().finish("test");
    }

}
