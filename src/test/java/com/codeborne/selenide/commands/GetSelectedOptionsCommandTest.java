package com.codeborne.selenide.commands;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockSelect;
import static com.codeborne.selenide.Mocks.option;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetSelectedOptionsCommandTest {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final GetSelectedOptions command = new GetSelectedOptions();

  @BeforeEach
  void setup() {
    when(locator.driver()).thenReturn(new DriverStub());
  }

  @Test
  void returnsOnlySelectedOptionsOfGivenSelect() {
    WebElement option1 = option("Leonardo", true);
    WebElement option2 = option("Raphael", false);
    WebElement option3 = option("Donatello", false);
    WebElement option4 = option("Michelangelo", true);
    SelenideElement select = mockSelect(option1, option2, option3, option4);
    when(locator.getWebElement()).thenReturn(select);

    ElementsCollection selectedOptions = command.execute(proxy, locator, null);

    assertThat(selectedOptions).isEqualTo(asList(option1, option4));
  }
}
