package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class SetValueCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final Clear clear = mock();
  private final SetValue command = new SetValue(clear);
  private final WebElement mockedFoundElement = mock();
  private final DriverStub driver = new DriverStub();

  @BeforeEach
  void setup() {
    when(locator.findAndAssertElementIsEditable()).thenReturn(mockedFoundElement);
    when(locator.driver()).thenReturn(driver);
  }

  @AfterEach
  void noMoreInteractions() {
    verify(locator).driver();
    verify(locator).findAndAssertElementIsEditable();
    verifyNoMoreInteractions(proxy, locator, mockedFoundElement);
  }

  @Test
  void clearsTheInputIfArgsTextIsEmpty() {
    WebElement returnedElement = command.execute(proxy, locator, new Object[]{""});
    assertThat(returnedElement).isEqualTo(proxy);
    verify(clear).clearAndTrigger(driver, mockedFoundElement);
  }

  @Test
  void typesGivenTextIntoInputField() {
    WebElement returnedElement = command.execute(proxy, locator, new Object[]{"Stalker"});
    assertThat(returnedElement).isEqualTo(proxy);
    verify(clear).clear(driver, mockedFoundElement);
    verify(mockedFoundElement).sendKeys("Stalker");
  }
}
