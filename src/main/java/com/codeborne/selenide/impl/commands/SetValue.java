package com.codeborne.selenide.impl.commands;

import com.codeborne.selenide.Command;
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
    if ("select".equalsIgnoreCase(element.getTagName())) {
      selectOptionByValue.execute(proxy, locator, args);
    }
    else if ("input".equalsIgnoreCase(element.getTagName()) && "radio".equals(element.getAttribute("type"))) {
      selectRadio.execute(proxy, locator, args);
    }
    else if (text == null || text.isEmpty()) {
      element.clear();
    }
    else if (fastSetValue) {
      text = truncateMaxLength(element, text);
      String error = executeJavaScript(
          "if (arguments[0].getAttribute('readonly') != undefined) " +
              "  return 'Cannot change value of readonly element';" +
              "arguments[0].value = arguments[1];" +
              "return null;", element, text);
      if (error != null) throw new InvalidStateException(error);
      events.fireEvent(element, "focus", "keydown", "keypress", "input", "keyup", "change");
    }
    else {
      element.clear();
      element.sendKeys(text);
      events.fireChangeEvent(element);
    }

    return proxy;
  }

  private String truncateMaxLength(WebElement element, String text) {
    try {
      String maxlength = element.getAttribute("maxlength");
      int elementMaxLength = Integer.parseInt(maxlength);
      return text.length() > elementMaxLength ? text.substring(0, elementMaxLength) : text;
    }
    catch (NumberFormatException invalidMaxLength) {
      return text;
    }
  }
}
