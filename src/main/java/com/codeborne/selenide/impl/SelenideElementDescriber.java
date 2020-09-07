package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SelenideElementDescriber implements ElementDescriber {
  @CheckReturnValue
  @Nonnull
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

  @CheckReturnValue
  @Nonnull
  @Override
  public String briefly(Driver driver, @Nonnull WebElement element) {
    try {
      if (element == null) {
        return "null";
      }
      if (element instanceof SelenideElement) {
        return briefly(driver, ((SelenideElement) element).toWebElement());
      }
      return new Describe(driver, element).attr("id").attr("name").flush();
    } catch (WebDriverException elementDoesNotExist) {
      return failedToDescribe(Cleanup.of.webdriverExceptionMessage(elementDoesNotExist));
    }
    catch (RuntimeException e) {
      return failedToDescribe(e.toString());
    }
  }

  @Nonnull
  private String failedToDescribe(String s2) {
    return "Ups, failed to described the element [caused by: " + s2 + ']';
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String selector(By selector) {
    return selector.toString()
      .replace("By.selector: ", "")
      .replace("By.cssSelector: ", "");
  }
}
