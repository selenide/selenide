package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetSelectedValueCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource selectElement = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private GetSelectedValue getSelectedValueCommand;
  private final GetSelectedOption getSelectedOptionCommand = mock(GetSelectedOption.class);

  @BeforeEach
  void setup() {
    getSelectedValueCommand = new GetSelectedValue(getSelectedOptionCommand);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    GetSelectedValue getSelectedText = new GetSelectedValue();
    Field getSelectedOptionField = getSelectedText.getClass().getDeclaredField("getSelectedOption");
    getSelectedOptionField.setAccessible(true);
    GetSelectedOption getSelectedOption = (GetSelectedOption) getSelectedOptionField.get(getSelectedText);
    assertThat(getSelectedOption)
      .isNotNull();
  }

  @Test
  void testExecuteMethodWhenSelectedOptionReturnsNothing() throws IOException {
    Object[] args = {"something more"};
    when(getSelectedOptionCommand.execute(proxy, selectElement, args)).thenReturn(null);
    assertThat(getSelectedValueCommand.execute(proxy, selectElement, args))
      .isNullOrEmpty();
  }

  @Test
  void testExecuteMethodWhenSelectedOptionReturnsElement() throws IOException {
    Object[] args = {"something more"};
    when(getSelectedOptionCommand.execute(proxy, selectElement, args)).thenReturn(mockedElement);
    String elementText = "Element text";
    when(mockedElement.getAttribute("value")).thenReturn(elementText);
    assertThat(getSelectedValueCommand.execute(proxy, selectElement, args))
      .isEqualTo(elementText);
  }
}
