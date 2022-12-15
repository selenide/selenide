package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.impl.Alias.NONE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

final class SelectRadioCommandTest {
  private final DriverStub driver = new DriverStub();
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final Click click = mock();
  private final SelectRadio command = new SelectRadio(click);

  @BeforeEach
  void setup() {
    when(locator.driver()).thenReturn(driver);
    when(locator.getAlias()).thenReturn(NONE);
  }

  @Test
  void noElementsIsFound() {
    when(locator.findAll()).thenReturn(emptyList());
    assertThatThrownBy(() -> command.execute(proxy, locator, new Object[]{"ElementValue"}))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {null}")
      .hasMessageContaining("Expected: value=\"ElementValue\"");
  }

  @Test
  void radioButtonIsReadOnly() {
    givenRadioInput("input.gender", "female", true);

    assertThatThrownBy(() -> command.execute(proxy, locator, new Object[]{"female"}))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageStartingWith("Invalid element state [input.gender]: Cannot select readonly radio button");

    verifyNoInteractions(click);
  }

  @Test
  void radioButton() {
    WebElement input = givenRadioInput(".btn.btn-primary", "Submit payment", false);

    SelenideElement clickedElement = command.execute(proxy, locator, new Object[]{"Submit payment"});
    assertThat(clickedElement.getWrappedElement()).isEqualTo(input);
    verify(click).click(driver, input);
  }

  private WebElement givenRadioInput(String selector, String value, boolean readonly) {
    WebElement input = mock();
    when(input.getAttribute("value")).thenReturn(value);
    if (readonly) {
      when(input.getAttribute("readonly")).thenReturn("true");
    }
    when(locator.findAll()).thenReturn(singletonList(input));
    when(locator.description()).thenReturn(selector);
    return input;
  }
}
