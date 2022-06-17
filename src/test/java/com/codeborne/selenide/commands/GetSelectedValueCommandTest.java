package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Mocks.mockElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class GetSelectedValueCommandTest {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource selectElement = mock(WebElementSource.class);
  private final GetSelectedOption getSelectedOptionCommand = mock(GetSelectedOption.class);
  private final GetSelectedValue command = new GetSelectedValue(getSelectedOptionCommand);

  @Test
  void selectedOptionReturnsNothing() {
    when(getSelectedOptionCommand.execute(any(), any(), any())).thenThrow(new NoSuchElementException("No options are selected"));
    assertThat(command.execute(proxy, selectElement, null)).isNull();
    verify(getSelectedOptionCommand).execute(proxy, selectElement, null);
  }

  @Test
  void selectedOptionReturnsElement() {
    SelenideElement option = mockElement("option", "Element text");
    when(option.getAttribute("value")).thenReturn("Element value");
    when(getSelectedOptionCommand.execute(any(), any(), any())).thenReturn(option);

    String selectedValue = command.execute(proxy, selectElement, null);

    assertThat(selectedValue).isEqualTo("Element value");
    verify(getSelectedOptionCommand).execute(proxy, selectElement, null);
  }
}
