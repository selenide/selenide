package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.DoesNotContainTextsError;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.ElementCommunicator;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

@ParametersAreNonnullByDefault
public class ContainExactTextsCaseSensitive extends CollectionCondition {
  private static final ElementCommunicator communicator = inject(ElementCommunicator.class);
  private final List<String> expectedTexts;

  public ContainExactTextsCaseSensitive(String... expectedTexts) {
    this(asList(expectedTexts));
  }

  public ContainExactTextsCaseSensitive(List<String> expectedTexts) {
    if (expectedTexts.isEmpty()) {
      throw new IllegalArgumentException("No expected texts given");
    }
    this.expectedTexts = unmodifiableList(expectedTexts);
  }

  @CheckReturnValue
  @Override
  @Nonnull
  public CheckResult check(Driver driver, List<WebElement> elements) {
    List<String> actualTexts = communicator.texts(driver, elements);
    List<String> difference = diff(expectedTexts, actualTexts);
    return new CheckResult(difference.isEmpty(), actualTexts);
  }

  @Override
  public void fail(CollectionSource collection,
                   CheckResult lastCheckResult,
                   @Nullable Exception cause,
                   long timeoutMs) {
    List<String> actualTexts = lastCheckResult.requireActualValue();

    if (actualTexts.isEmpty()) {
      throw new ElementNotFound(collection, toString(), timeoutMs, cause);
    }
    else {
      List<String> difference = diff(expectedTexts, actualTexts);
      throw new DoesNotContainTextsError(collection,
        expectedTexts, actualTexts, difference, explanation,
        timeoutMs, cause);
    }
  }

  @Nonnull
  private static List<String> diff(List<String> expectedTexts, List<String> actualTexts) {
    List<String> difference = new ArrayList<>(expectedTexts.size());
    expectedTexts.forEach(text -> difference.add(Html.text.reduceSpaces(text)));
    actualTexts.forEach(text -> difference.remove(Html.text.reduceSpaces(text)));
    return difference;
  }

  @Override
  public boolean missingElementSatisfiesCondition() {
    return false;
  }

  @Override
  public String toString() {
    return "Contains exact texts case-sensitive " + expectedTexts;
  }
}
