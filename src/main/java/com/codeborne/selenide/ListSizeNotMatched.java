package com.codeborne.selenide;

import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;

public class ListSizeNotMatched extends AssertionError {
  public ListSizeNotMatched(int expectedSize, List<WebElement> actualElements, long timeoutMs) {
    super(": expected: " + expectedSize +
        ", actual: " + actualElements.size() +
        ", timeout: " + timeoutMs/1000 + " s." +
        "\nElements: " + elementsToString(actualElements));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getMessage();
  }
}
