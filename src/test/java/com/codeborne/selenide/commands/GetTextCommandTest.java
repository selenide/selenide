package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetTextCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final GetSelectedText getSelectedTextCommand = mock(GetSelectedText.class);
  private final GetText getTextCommand = new GetText(getSelectedTextCommand);

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void testDefaultConstructor() throws IllegalAccessException, NoSuchFieldException {
    GetText getText = new GetText();
    Field getSelectedTextField = getText.getClass().getDeclaredField("getSelectedText");
    getSelectedTextField.setAccessible(true);
    GetSelectedText getSelectedText = (GetSelectedText) getSelectedTextField.get(getText);
    assertThat(getSelectedText)
      .isNotNull();
  }

  @Test
  void testExecuteMethodWithSelectElement() {
    when(mockedElement.getTagName()).thenReturn("select");
    Object[] args = {"something more"};
    String selectedElementText = "Selected Text";
    when(getSelectedTextCommand.execute(proxy, locator, args)).thenReturn(selectedElementText);
    assertThat(getTextCommand.execute(proxy, locator, args))
      .isEqualTo(selectedElementText);
  }

  @Test
  void testExecuteMethodWithNotSelectElement() {
    when(mockedElement.getTagName()).thenReturn("href");
    String text = "This is text";
    when(mockedElement.getText()).thenReturn(text);
    assertThat(getTextCommand.execute(proxy, locator, new Object[]{"something more"}))
      .isEqualTo(text);
  }
}
