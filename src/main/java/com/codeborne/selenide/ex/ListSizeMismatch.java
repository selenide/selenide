package com.codeborne.selenide.ex;

import com.codeborne.selenide.collections.ListSize.Operator;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;

public class ListSizeMismatch extends UIAssertionError {
  public ListSizeMismatch(Operator operator, int expectedSize,
                          WebElementsCollection collection, List<WebElement> actualElements,
                          Exception lastError, long timeoutMs) {
    super(": expected: " + operator.sign + " " + expectedSize +
        ", actual: " + (actualElements == null ? 0 : actualElements.size()) +
        ", collection: " + collection.description() +
        "\nElements: " + elementsToString(actualElements), lastError
    );
    super.timeoutMs = timeoutMs;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getMessage() + uiDetails();
  }
}
