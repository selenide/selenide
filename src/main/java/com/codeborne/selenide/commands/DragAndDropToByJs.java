package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.FileContent;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DragAndDropToByJs implements Command<SelenideElement> {

  @Nullable
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) throws IOException {
    SelenideElement target = ElementFinder.wrap(locator.driver(), By.cssSelector((String) args[0]));
    locator.findAndAssertElementIsInteractable();
    target.shouldBe(Condition.visible);
    executeDragAndDropJsScript(locator.driver(), locator.getWebElement(), target.toWebElement());
    return proxy;
  }

  private void executeDragAndDropJsScript(Driver driver, WebElement from, WebElement to) {
    StringBuilder js = new StringBuilder();
    js.append(new FileContent("drag_and_drop_script").content());
    js.append(String.format("$('%s').simulateDragDrop({ dropTarget: '%s'});", extractSelector(from), extractSelector(to)));
    driver.executeJavaScript(js.toString());
  }

  @Nonnull
  @CheckReturnValue
  private String extractSelector(WebElement webElement) {
    Pattern pattern = Pattern.compile("(-> .*: )(.*)(])");
    Matcher matcher = pattern.matcher(webElement.toString());
    if (!matcher.find()) throw new IllegalArgumentException("Can't parse css selector!");
    return matcher.group(2);
  }
}
