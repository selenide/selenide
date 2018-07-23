package com.codeborne.selenide.commands;

import java.lang.reflect.Field;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private Val valCommand;
  private GetValue mockedGetValue;
  private SetValue mockedSetValue;

  @BeforeEach
  void setup() {
    mockedGetValue = mock(GetValue.class);
    mockedSetValue = mock(SetValue.class);
    valCommand = new Val(mockedGetValue, mockedSetValue);
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    Val val = new Val();
    Field getValueField = val.getClass().getDeclaredField("getValue");
    Field setValueField = val.getClass().getDeclaredField("setValue");
    getValueField.setAccessible(true);
    setValueField.setAccessible(true);
    GetValue getValue = (GetValue) getValueField.get(val);
    SetValue setValue = (SetValue) setValueField.get(val);

    assertThat(getValue)
      .isNotNull();
    assertThat(setValue)
      .isNotNull();
  }

  @Test
  void testExecuteValueWithNoArgs() {
    String getValueResult = "getValueResult";
    when(mockedGetValue.execute(proxy, locator, Command.NO_ARGS)).thenReturn(getValueResult);
    assertThat(valCommand.execute(proxy, locator, null))
      .isEqualTo(getValueResult);
    assertThat(valCommand.execute(proxy, locator, new Object[]{}))
      .isEqualTo(getValueResult);
  }

  @Test
  void testExecuteValueWithArgs() {
    assertThat(valCommand.execute(proxy, locator, new Object[]{"value"}))
      .isEqualTo(proxy);
  }
}
