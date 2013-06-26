package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;

public class ListSizeMismatch extends AssertionError {
  public ListSizeMismatch(int expectedSize, WebElementsCollection collection, List<WebElement> actualElements, long timeoutMs) {
    super(": expected: " + expectedSize +
        ", actual: " + actualElements.size() +
        ", collection: " + collection.description() +
        ", timeout: " + timeoutMs/1000 + " s." +
        "\nElements: " + elementsToString(actualElements));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getMessage();
  }
}
