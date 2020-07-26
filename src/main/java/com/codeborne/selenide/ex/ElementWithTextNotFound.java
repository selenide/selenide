package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.List;

import static java.lang.System.lineSeparator;

public class ElementWithTextNotFound extends UIAssertionError {

  public ElementWithTextNotFound(WebElementsCollection collection, List<String> actualTexts,
                                 List<String> expectedTexts, String explanation, long timeoutMs) {
    super(collection.driver(),
      "Element with text not found" +
        lineSeparator() + "Actual: " + actualTexts +
        lineSeparator() + "Expected: " + expectedTexts +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description());
    super.timeoutMs = timeoutMs;
  }
}
