package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.Mocks;
import com.codeborne.selenide.impl.Alias;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Condition.visible;
import static java.lang.System.lineSeparator;
import static org.assertj.core.api.Assertions.assertThat;

final class ElementShouldTest {
  private final Driver driver = new DriverStub();
  private final WebElement webElement = Mocks.mockWebElement("h1", "Hello boy");

  @Test
  void testToString() {
    String searchCriteria = By.name("selenide").toString();
    Exception cause = new NoSuchElementException("By.name: q");
    CheckResult checkResult = new CheckResult(REJECT, "visible:false");
    ElementShould elementShould = new ElementShould(driver, Alias.NONE, searchCriteria, "be ", visible, checkResult, webElement, cause);

    assertThat(elementShould)
      .hasMessage("Element should be visible {By.name: selenide}" + lineSeparator() +
        "Element: '<h1>Hello boy</h1>'" + lineSeparator() +
        "Actual value: visible:false" + lineSeparator() +
        "Timeout: 0 ms." + lineSeparator() +
        "Caused by: NoSuchElementException: By.name: q");
  }
}
