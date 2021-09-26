package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class SelectRadioCommandTest {
  private final DriverStub driver = new DriverStub();
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelectRadio command = new SelectRadio();

  @BeforeEach
  void setup() {
    when(locator.driver()).thenReturn(driver);
  }

  @Test
  void noElementsIsFound() {
    when(locator.findAll()).thenReturn(emptyList());
    assertThatThrownBy(() -> command.execute(proxy, locator, new Object[]{"ElementValue"}))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {null}")
      .hasMessageContaining("Expected: value 'ElementValue'");
  }

  @Test
  void radioButtonIsReadOnly() {
    givenRadioInput("ElementValue", true);

    assertThatThrownBy(() -> command.execute(proxy, locator, new Object[]{"ElementValue"}))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageStartingWith("Invalid element state: Cannot select readonly radio button");
  }

  @Test
  void radioButton() {
    WebElement input = givenRadioInput("ElementValue", false);

    SelenideElement clickedElement = command.execute(proxy, locator, new Object[]{"ElementValue"});
    assertThat(clickedElement.getWrappedElement()).isEqualTo(input);
  }

  private WebElement givenRadioInput(String value, boolean readonly) {
    WebElement input = mock(WebElement.class);
    when(input.getAttribute("value")).thenReturn(value);
    if (readonly) {
      when(input.getAttribute("readonly")).thenReturn("true");
    }
    when(locator.findAll()).thenReturn(singletonList(input));
    return input;
  }
}
