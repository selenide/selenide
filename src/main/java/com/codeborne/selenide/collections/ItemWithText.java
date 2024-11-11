package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.ElementWithTextNotFound;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.ElementCommunicator;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Collections.singletonList;

public class ItemWithText extends WebElementsCondition {
  private static final ElementCommunicator communicator = inject(ElementCommunicator.class);
  private final String expectedText;

  public ItemWithText(String expectedText) {
    this.expectedText = expectedText;
  }

  @Override
  public CheckResult check(CollectionSource collection) {
    List<WebElement> elements = collection.getElements();
    List<String> texts = communicator.texts(collection.driver(), elements);

    return new CheckResult(texts.contains(expectedText), texts);
  }

  @Override
  public void fail(CollectionSource collection,
                   CheckResult lastCheckResult,
                   @Nullable Exception cause,
                   long timeoutMs) {
    throw new ElementWithTextNotFound(
      collection, singletonList(expectedText), lastCheckResult.getActualValue(), explanation, timeoutMs, cause);
  }

  @Override
  public String toString() {
    return "Text " + expectedText;
  }
}
