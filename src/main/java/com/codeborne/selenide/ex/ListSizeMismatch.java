package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;

public class ListSizeMismatch extends UIAssertionError {
  public ListSizeMismatch(Driver driver, String operator, int expectedSize, String explanation, WebElementsCollection collection,
                          List<WebElement> actualElements, Exception lastError, long timeoutMs) {
    super(driver,
      ": expected: " + operator + " " + expectedSize +
        (explanation == null ? "" : " (because " + explanation + ")") +
        ", actual: " + (actualElements == null ? 0 : actualElements.size()) +
        ", collection: " + collection.description() +
        "\nElements: " + elementsToString(collection.driver(), actualElements), lastError
    );
    super.timeoutMs = timeoutMs;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getMessage() + uiDetails();
  }
}
