package com.codeborne.selenide.appium;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class CalculatorTest {

    @Before
    public void setUp() {
        Configuration.browser = AndroidDriverProvider.class.getName();
    }

    @Test
    public void plain(){
        $(By.id("com.android.calculator2:id/digit2")).click();
        $(By.id("com.android.calculator2:id/plus")).click();
        $(By.id("com.android.calculator2:id/digit4")).click();
        $(By.id("com.android.calculator2:id/equal")).click();

        $(By.className("android.widget.EditText")).shouldHave(text("6"));
    }

    @Test @Ignore("not implemented yet")
    public void pageObject(){
        CalculatorPage page = new CalculatorPage();
        page.number2.click();
        page.plus.click();
        page.number4.click();
        page.equal.click();
        
        page.result.shouldHave(text("6"));
    }
}
