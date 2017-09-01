package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Command.NO_ARGS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SetSelectedTest {
  private WebElement textbox = mock(WebElement.class);
  private WebElement button = mock(WebElement.class);
  private WebElement checkbox = mock(WebElement.class);
  private WebElement radio = mock(WebElement.class);
  private WebElement option = mock(WebElement.class);
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource locator = mock(WebElementSource.class);

  private SetSelected command = new SetSelected();
  private boolean selected;

  @Before
  public void setUp() {
    selected = false;
    command.click = mock(Click.class);
    doAnswer(invocation -> {
      WebElement element = ((WebElementSource) invocation.getArgument(1)).getWebElement();
      selected = !selected;
      doReturn(selected).when(element).isSelected();
      return null;
    }).when(command.click).execute(any(SelenideElement.class), any(WebElementSource.class), any());
    doReturn("input").when(textbox).getTagName();
    doReturn("button").when(button).getTagName();
    doReturn("input").when(checkbox).getTagName();
    doReturn("checkbox").when(checkbox).getAttribute("type");
    doReturn("input").when(radio).getTagName();
    doReturn("radio").when(radio).getAttribute("type");
    doReturn("option").when(option).getTagName();
  }

  @Test(expected = InvalidStateException.class)
  public void failsToSelectTextbox() {
    doReturn(textbox).when(locator).getWebElement();
    command.execute(proxy, locator, new Object[]{true});
    verify(command.click, never()).execute(proxy, locator, NO_ARGS);
  }

  @Test(expected = InvalidStateException.class)
  public void failsToSelectButton() {
    doReturn(button).when(locator).getWebElement();
    command.execute(proxy, locator, new Object[]{true});
    verify(command.click, never()).execute(proxy, locator, NO_ARGS);
  }

  @Test
  public void selectsCheckbox() {
    doReturn(checkbox).when(locator).getWebElement();
    command.execute(proxy, locator, new Object[]{true});
    verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);
    command.execute(proxy, locator, new Object[]{true});
    verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);

    command.execute(proxy, locator, new Object[]{false});
    verify(command.click, times(2)).execute(proxy, locator, NO_ARGS);
    command.execute(proxy, locator, new Object[]{false});
    verify(command.click, times(2)).execute(proxy, locator, NO_ARGS);
  }

  @Test(expected = InvalidStateException.class)
  public void failsToSelectCheckbox() {
    doReturn(checkbox).when(locator).getWebElement();
    doReturn("true").when(checkbox).getAttribute("readonly");
    command.execute(proxy, locator, new Object[]{true});
    verify(command.click, never()).execute(proxy, locator, NO_ARGS);
  }

  @Test
  public void selectsRadio() {
    doReturn(radio).when(locator).getWebElement();
    command.execute(proxy, locator, new Object[]{true});
    verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);
    command.execute(proxy, locator, new Object[]{true});
    verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);
  }

  @Test(expected = InvalidStateException.class)
  public void failsToSelectRadio() {
    doReturn(radio).when(locator).getWebElement();
    doReturn("true").when(radio).getAttribute("readonly");
    command.execute(proxy, locator, new Object[]{true});
  }

  @Test(expected = InvalidStateException.class)
  public void failsToDeselectRadio() {
    doReturn(radio).when(locator).getWebElement();
    command.execute(proxy, locator, new Object[]{true});
    command.execute(proxy, locator, new Object[]{false});
  }

  @Test
  public void selectsOption() {
    doReturn(option).when(locator).getWebElement();
    command.execute(proxy, locator, new Object[]{true});
    verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);
    command.execute(proxy, locator, new Object[]{true});
    verify(command.click, times(1)).execute(proxy, locator, NO_ARGS);
  }

  @Test(expected = InvalidStateException.class)
  public void failsToDeselectOption() {
    doReturn(option).when(locator).getWebElement();
    command.execute(proxy, locator, new Object[]{true});
    command.execute(proxy, locator, new Object[]{false});
  }
}
