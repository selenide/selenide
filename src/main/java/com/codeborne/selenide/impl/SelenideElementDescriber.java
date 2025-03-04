package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class SelenideElementDescriber implements ElementDescriber {
  @Override
  public String fully(Driver driver, @Nullable WebElement element) {
    try {
      if (element == null) {
        return "null";
      }
      return new Describe(driver, element)
        .appendAttributes()
        .isSelected(element)
        .isDisplayed(element)
        .serialize();
    } catch (WebDriverException elementDoesNotExist) {
      return failedToDescribe(Cleanup.of.webdriverExceptionMessage(elementDoesNotExist));
    }
    catch (RuntimeException e) {
      return failedToDescribe(e.toString());
    }
  }

  @Override
  public String briefly(Driver driver, WebElement element) {
    try {
      //noinspection ConstantValue
      if (element == null) {
        return "null";
      }
      if (element instanceof SelenideElement selenideElement) {
        return briefly(driver, selenideElement.toWebElement());
      }
      return new Describe(driver, element).attr("id").attr("name").flush();
    } catch (WebDriverException elementDoesNotExist) {
      return failedToDescribe(Cleanup.of.webdriverExceptionMessage(elementDoesNotExist));
    }
    catch (RuntimeException e) {
      return failedToDescribe(e.toString());
    }
  }

  private String failedToDescribe(String s2) {
    return "Ups, failed to describe the element [caused by: " + s2 + ']';
  }

  @Override
  public String selector(By selector) {
    return selector.toString()
      .replace("By.selector: ", "")
      .replace("By.cssSelector: ", "")
      .replace("By.tagName: ", "")
      .replace("By.id: ", "#");
  }
}
