package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;

public class ListSizeMismatch extends UIAssertionError {
  public ListSizeMismatch(int expectedSize, WebElementsCollection collection, List<WebElement> actualElements,
                          Exception lastError, long timeoutMs) {
    super(": expected: " + expectedSize +
        ", actual: " + (actualElements == null ? 0 : actualElements.size()) +
        ", collection: " + collection.description() +
        "\nElements: " + elementsToString(actualElements), timeoutMs, lastError
    );
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getMessage();
  }
}
