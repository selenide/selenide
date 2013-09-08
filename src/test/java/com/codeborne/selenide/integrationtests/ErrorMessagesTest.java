package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementMismatch;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getElement;
import static com.codeborne.selenide.Selenide.open;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ErrorMessagesTest {
    PageObject pageObject = open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"), PageObject.class);

    @Test
    public void elementTextDoesNotMatch() {
        try {
            $("h2").shouldHave(text("expected text"));
            fail("Expected ElementMismatch");
        } catch (ElementMismatch expected) {
            assertEquals("ElementMismatch {By.selector: h2}\n" +
                    "Expected: got text 'expected text'\n" +
                    "Element: '<h2>Dropdown list</h2>'\n" +
                    "Timeout: 4 s.", expected.toString());
        }
    }

    @Test
    public void wrapperTextDoesNotMatch() {
        try {
            $(getElement(By.tagName("h2"))).shouldHave(text("expected text"));
            fail("Expected ElementMismatch");
        } catch (ElementMismatch expected) {
            assertEquals("ElementMismatch {By.tagName: h2}\n" +
                    "Expected: got text 'expected text'\n" +
                    "Element: '<h2>Dropdown list</h2>'\n" +
                    "Timeout: 4 s.", expected.toString());
        }
    }

    @Test
    public void pageObjectElementTextDoesNotMatch() {
        try {
            $(pageObject.header1).shouldHave(text("expected text"));
            fail("Expected ElementMismatch");
        } catch (ElementMismatch expected) {
            assertEquals("ElementMismatch {By.tagName: h2}\n" +
                    "Expected: got text 'expected text'\n" +
                    "Element: '<h2>Dropdown list</h2>'\n" +
                    "Timeout: 4 s.", expected.toString());
        }
    }

    @Test
    public void pageObjectWrapperTextDoesNotMatch() {
        try {
            $(pageObject.header2).shouldHave(text("expected text"));
            fail("Expected ElementMismatch");
        } catch (ElementMismatch expected) {
            assertEquals("ElementMismatch {By.tagName: h2: <h2>Dropdown list</h2>}\n" +
                    "Expected: got text 'expected text'\n" +
                    "Element: '<h2>Dropdown list</h2>'\n" +
                    "Timeout: 4 s.", expected.toString());
        }
    }

    public static class PageObject {
        @FindBy(tagName="h2") public SelenideElement header1;
        @FindBy(tagName="h2") public WebElement header2;
    }
}
