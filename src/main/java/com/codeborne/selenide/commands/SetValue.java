package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Configuration.fastSetValue;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.impl.Events.events;

public class SetValue implements Command<WebElement> {
  SelectOptionByValue selectOptionByValue = new SelectOptionByValue();
  SelectRadio selectRadio = new SelectRadio();

  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String text = (String) args[0];
    WebElement element = locator.findAndAssertElementIsVisible();

    if (Configuration.versatileSetValue
            && "select".equalsIgnoreCase(element.getTagName())) {
      selectOptionByValue.execute(proxy, locator, args);
      return proxy;
    }
    if (Configuration.versatileSetValue
            && "input".equalsIgnoreCase(element.getTagName()) && "radio".equals(element.getAttribute("type"))) {
      selectRadio.execute(proxy, locator, args);
      return proxy;
    }

    setValueForTextInput(element, text);
    return proxy;
  }

  private void setValueForTextInput(WebElement element, String text) {
    if (text == null || text.isEmpty()) {
      element.clear();
    } else if (fastSetValue) {
      String error = setValueByJs(element, text);
      if (error != null) throw new InvalidStateException(error);
      events.fireEvent(element, "focus", "keydown", "keypress", "input", "keyup", "change");
    } else {
      element.clear();
      element.sendKeys(text);
      events.fireChangeEvent(element);
    }
  }

  private String setValueByJs(WebElement element, String text) {
    return executeJavaScript(
            "return (function(webelement, text) {" +
              "if (webelement.getAttribute('readonly') != undefined) return 'Cannot change value of readonly element';" +
              "var maxlength = webelement.getAttribute('maxlength') == null ? -1 : parseInt(webelement.getAttribute('maxlength'));" +
              "webelement.value = " +
                    "maxlength == -1 ? text " +
                      ": text.length <= maxlength ? text " +
                        ": text.substring(0, maxlength);" +
              "return null;" +
            "})(arguments[0], arguments[1]);"
            , element, text);
  }
}
