package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;

public class MatcherError extends UIAssertionError {

  public MatcherError(String matcher, String predicateDescription, String explanation,
                      WebElementsCollection collection, List<WebElement> actualElements, Exception lastError, long timeoutMs) {
    super(collection.driver(),
      "Collection matcher error" +
        "\nExpected: " + matcher + " of elements to match [" + predicateDescription + "] predicate" +
        (explanation == null ? "" : "\nBecause: " + explanation) +
        "\nCollection: " + collection.description() +
        "\nElements: " + elementsToString(collection.driver(), actualElements), lastError
    );
    super.timeoutMs = timeoutMs;
  }

}
