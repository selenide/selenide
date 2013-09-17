package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementMismatch;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.*;

public class ErrorMessagesTest {
  PageObject pageObject = open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"), PageObject.class);

  @Test
  public void elementTextDoesNotMatch() {
    try {
      $("h2").shouldHave(text("expected text"));
      fail("Expected ElementMismatch");
    } catch (ElementMismatch expected) {
      assertEquals("ElementMismatch {By.selector: h2}\n" +
          "Expected: text 'expected text'\n" +
          "Element: '<h2>Dropdown list</h2>'\n" +
          "Timeout: 4 s.", expected.toString());
    }
  }

  @Test
  public void elementAttributeDoesNotMatch() {
    try {
      $("h2").shouldHave(attribute("name", "header"));
      fail("Expected ElementMismatch");
    } catch (ElementMismatch expected) {
      assertEquals("ElementMismatch {By.selector: h2}\n" +
          "Expected: attribute name=header\n" +
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
          "Expected: text 'expected text'\n" +
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
          "Expected: text 'expected text'\n" +
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
          "Expected: text 'expected text'\n" +
          "Element: '<h2>Dropdown list</h2>'\n" +
          "Timeout: 4 s.", expected.toString());
    }
  }

  @Test
  public void selectOptionFromUnexistingList() {
    try {
      $(pageObject.categoryDropdown).selectOption("SomeOption");
    } catch (ElementNotFound e) {
      assertContains(e, "ElementNotFound {By.id: invalid_id: ElementNotFound}", "Expected: present");
    }
  }

  @Test
  public void clickUnexistingWrappedElement() {
    try {
      $(pageObject.categoryDropdown).click();
      fail("Expected ElementNotFound");
    } catch (ElementNotFound e) {
      assertEquals("ElementNotFound {By.id: invalid_id: ElementNotFound}\n" +
          "Expected: visible\n" +
          "Timeout: 4 s.", e.toString());
    }
  }

  private void assertContains(AssertionError e, String... expectedTexts) {
    for (String expectedText : expectedTexts) {
      assertTrue("Error message: " + e.toString() + " does not contain " + expectedText, e.toString().contains(expectedText));
    }
  }

  public static class PageObject {
    @FindBy(tagName = "h2")
    public SelenideElement header1;

    @FindBy(tagName = "h2")
    public WebElement header2;

    @FindBy(id = "invalid_id")
    private WebElement categoryDropdown;
  }
}
