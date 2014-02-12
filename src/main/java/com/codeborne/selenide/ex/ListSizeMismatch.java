package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;
import static com.codeborne.selenide.ex.ErrorMessages.screenshot;
import static com.codeborne.selenide.ex.ErrorMessages.timeout;

public class ListSizeMismatch extends AssertionError {
  public ListSizeMismatch(int expectedSize, WebElementsCollection collection, List<WebElement> actualElements, long timeoutMs) {
    super(": expected: " + expectedSize +
        ", actual: " + (actualElements == null ? 0 : actualElements.size()) +
        ", collection: " + collection.description() +
        screenshot() +
        timeout(timeoutMs) +
        "\nElements: " + elementsToString(actualElements)
    );
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getMessage();
  }
}
