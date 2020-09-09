package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementWithTextNotFound;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.util.Collections.singletonList;

@ParametersAreNonnullByDefault
public class ItemWithText extends CollectionCondition {

  private final String expectedText;

  public ItemWithText(String expectedText) {
    this.expectedText = expectedText;
  }

  @CheckReturnValue
  @Override
  public boolean test(List<WebElement> elements) {
    return ElementsCollection
      .texts(elements)
      .contains(expectedText);
  }

  @Override
  public void fail(WebElementsCollection collection,
                   @Nullable List<WebElement> elements,
                   @Nullable Exception lastError,
                   long timeoutMs) {
    throw new ElementWithTextNotFound(
      collection, ElementsCollection.texts(elements), singletonList(expectedText), explanation, timeoutMs, lastError);
  }

  @CheckReturnValue
  @Override
  public boolean applyNull() {
    return false;
  }

  @CheckReturnValue
  @Override
  public String toString() {
    return "Text " + expectedText;
  }
}
