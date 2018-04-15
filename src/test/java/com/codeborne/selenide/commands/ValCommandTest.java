package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private Val valCommand;
  private GetValue mockedGetValue;
  private SetValue mockedSetValue;

  @Before
  public void setup() {
    mockedGetValue = mock(GetValue.class);
    mockedSetValue = mock(SetValue.class);
    valCommand = new Val(mockedGetValue, mockedSetValue);
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
  }

  @Test
  public void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    Val val = new Val();
    Field getValueField = val.getClass().getDeclaredField("getValue");
    Field setValueField = val.getClass().getDeclaredField("setValue");
    getValueField.setAccessible(true);
    setValueField.setAccessible(true);
    GetValue getValue = (GetValue) getValueField.get(val);
    SetValue setValue = (SetValue) setValueField.get(val);

    assertNotNull(getValue);
    assertNotNull(setValue);
  }

  @Test
  public void testExecuteValueWithNoArgs() {
    String getValueResult = "getValueResult";
    when(mockedGetValue.execute(proxy, locator, Command.NO_ARGS)).thenReturn(getValueResult);
    assertEquals(getValueResult, valCommand.execute(proxy, locator, null));
    assertEquals(getValueResult, valCommand.execute(proxy, locator, new Object[]{}));
  }

  @Test
  public void testExecuteValueWithArgs() {
    assertEquals(proxy, valCommand.execute(proxy, locator, new Object[]{"value"}));
  }
}
