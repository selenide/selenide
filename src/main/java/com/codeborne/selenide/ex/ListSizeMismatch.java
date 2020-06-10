package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.ElementsCollection.elementsToString;
import static java.lang.System.lineSeparator;

@ParametersAreNonnullByDefault
public class ListSizeMismatch extends UIAssertionError {
  public ListSizeMismatch(Driver driver, String operator, int expectedSize,
                          @Nullable String explanation,
                          WebElementsCollection collection,
                          @Nullable List<WebElement> actualElements,
                          @Nullable Exception lastError,
                          long timeoutMs) {
    super(driver,
      "List size mismatch: expected: " + operator + ' ' + expectedSize +
        (explanation == null ? "" : " (because " + explanation + ")") +
        ", actual: " + (actualElements == null ? 0 : actualElements.size()) +
        ", collection: " + collection.description() +
        lineSeparator() + "Elements: " + elementsToString(collection.driver(), actualElements), lastError
    );
    super.timeoutMs = timeoutMs;
  }
}
