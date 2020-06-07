package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.impl.Events.events;

@ParametersAreNonnullByDefault
public class SetValue implements Command<WebElement> {
  private final SelectOptionByValue selectOptionByValue;
  private final SelectRadio selectRadio;

  public SetValue() {
    this.selectOptionByValue = new SelectOptionByValue();
    this.selectRadio = new SelectRadio();
  }

  public SetValue(SelectOptionByValue selectOptionByValue, SelectRadio selectRadio) {
    this.selectOptionByValue = selectOptionByValue;
    this.selectRadio = selectRadio;
  }

  @Override
  @Nonnull
  public WebElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    String text = firstOf(args);
    WebElement element = locator.findAndAssertElementIsInteractable();

    if (locator.driver().config().versatileSetValue()
      && "select".equalsIgnoreCase(element.getTagName())) {
      selectOptionByValue.execute(proxy, locator, args);
      return proxy;
    }
    if (locator.driver().config().versatileSetValue()
      && "input".equalsIgnoreCase(element.getTagName()) && "radio".equals(element.getAttribute("type"))) {
      selectRadio.execute(proxy, locator, args);
      return proxy;
    }

    setValueForTextInput(locator.driver(), element, text);
    return proxy;
  }

  private void setValueForTextInput(Driver driver, WebElement element, String text) {
    if (text == null || text.isEmpty()) {
      element.clear();
    }
    else if (driver.config().fastSetValue()) {
      String error = setValueByJs(driver, element, text);
      if (error != null) throw new InvalidStateException(driver, error);
      else {
        events.fireEvent(driver, element, "keydown", "keypress", "input", "keyup", "change");
      }
    }
    else {
      element.clear();
      element.sendKeys(text);
    }
  }

  private String setValueByJs(Driver driver, WebElement element, String text) {
    return driver.executeJavaScript(
        "return (function(webelement, text) {" +
            "if (webelement.getAttribute('readonly') != undefined) return 'Cannot change value of readonly element';" +
            "if (webelement.getAttribute('disabled') != undefined) return 'Cannot change value of disabled element';" +
            "webelement.focus();" +
            "var maxlength = webelement.getAttribute('maxlength') == null ? -1 : parseInt(webelement.getAttribute('maxlength'));" +
            "webelement.value = " +
            "maxlength == -1 ? text " +
            ": text.length <= maxlength ? text " +
            ": text.substring(0, maxlength);" +
            "return null;" +
            "})(arguments[0], arguments[1]);",
        element, text);
  }
}
