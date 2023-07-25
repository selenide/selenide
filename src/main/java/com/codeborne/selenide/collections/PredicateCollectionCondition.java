package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.MatcherError;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.ElementDescriber;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;

import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public abstract class PredicateCollectionCondition extends CollectionCondition {
  private static final ElementDescriber describe = inject(ElementDescriber.class);

  protected final String matcher;
  protected final String description;
  protected final Predicate<WebElement> predicate;

  protected PredicateCollectionCondition(String matcher, String description, Predicate<WebElement> predicate) {
    this.matcher = matcher;
    this.description = description;
    this.predicate = predicate;
  }

  @Override
  public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause, long timeoutMs) {
    List<WebElement> elements = lastCheckResult.getActualValue();
    if (elements == null || elements.isEmpty()) {
      throw new ElementNotFound(collection, toString(), timeoutMs, cause);
    }
    else {
      throw new MatcherError(explanation, toString(),
        describe.fully(collection.driver(), elements),
        collection, cause, timeoutMs);
    }
  }

  @Override
  public boolean missingElementSatisfiesCondition() {
    return false;
  }

  @Override
  public String toString() {
    return String.format("%s elements to match [%s] predicate", matcher, description);
  }
}
