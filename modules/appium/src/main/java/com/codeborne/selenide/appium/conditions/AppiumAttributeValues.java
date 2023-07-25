package com.codeborne.selenide.appium.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.collections.ExactTexts;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
public class AppiumAttributeValues extends ExactTexts {

  protected final CombinedAttribute attribute;

  public AppiumAttributeValues(CombinedAttribute attribute, String... expectedAttributeValues) {
    super(expectedAttributeValues);
    this.attribute = attribute;
  }

  public AppiumAttributeValues(CombinedAttribute attribute, List<String> expectedAttributeValues) {
    super(expectedAttributeValues);
    this.attribute = attribute;
  }

  @Override
  @Nonnull
  @CheckReturnValue
  public CheckResult check(Driver driver, List<WebElement> elements) {
    if (elements.size() != this.expectedTexts.size()) {
      return new CheckResult(REJECT, elements.size());
    }
    else {
      List<String> actualAttributeValues = getActualAttributes(driver, elements);
      for (int i = 0; i < this.expectedTexts.size(); ++i) {
        String expectedText = this.expectedTexts.get(i);
        String actualAttributeValue = actualAttributeValues.get(i);
        if (!Objects.equals(expectedText, actualAttributeValue)) {
          return new CheckResult(REJECT, actualAttributeValues);
        }
      }

      return new CheckResult(ACCEPT, null);
    }
  }

  private List<String> getActualAttributes(Driver driver, List<WebElement> elements) {
    return elements.stream()
      .map(element -> attribute.getAttributeValue(driver, element))
      .collect(toList());
  }
}
