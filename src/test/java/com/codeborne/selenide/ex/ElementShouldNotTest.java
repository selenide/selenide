package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.impl.Alias;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Condition.visible;
import static java.lang.System.lineSeparator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

final class ElementShouldNotTest {
  private final Driver driver = new DriverStub();

  @Test
  void testToString() {
    CheckResult checkResult = new CheckResult(REJECT, "visible:false");
    ElementShouldNot elementShould = new ElementShouldNot(driver, Alias.NONE, "by.name: selenide", "be ", visible, checkResult,
      mock(), new Throwable("Error message"));
    assertThat(elementShould).hasMessage("Element should not be visible {by.name: selenide}" + lineSeparator() +
      "Element: '<null displayed:false></null>'" + lineSeparator() +
      "Actual value: visible:false" + lineSeparator() +
      "Timeout: 0 ms." + lineSeparator() +
      "Caused by: java.lang.Throwable: Error message");
  }
}
