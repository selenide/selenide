package com.codeborne.selenide.appium.conditions;

import com.codeborne.selenide.appium.AppiumDriverRunner;
import com.codeborne.selenide.collections.ExactTexts;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AppiumAttributeValues extends ExactTexts {

  protected String androidAttributeName;
  protected String iosAttributeName;

  public AppiumAttributeValues(String androidAttributeName, String iosAttributeName, String... expectedAttributeValues) {
    super(expectedAttributeValues);
    this.androidAttributeName = androidAttributeName;
    this.iosAttributeName = iosAttributeName;
  }

  public AppiumAttributeValues(String androidAttributeName, String iosAttributeName, List<String> expectedAttributeValues) {
    super(expectedAttributeValues);
    this.androidAttributeName = androidAttributeName;
    this.iosAttributeName = iosAttributeName;
  }

  @Override
  public boolean test(List<WebElement> elements) {

    if (elements.size() != this.expectedTexts.size()) {
      return false;
    } else {
      List<String> actualAttributeValues = getActualAttributeValuesToCompare(elements);
      for (int i = 0; i < this.expectedTexts.size(); ++i) {
        String expectedText = this.expectedTexts.get(i);
        String actualAttributeValue = actualAttributeValues.get(i);
        if (!expectedText.equals(actualAttributeValue)) {
          return false;
        }
      }

      return true;
    }
  }

  private List<String> getActualAttributeValuesToCompare(List<WebElement> elements) {
    return elements.stream().map(getFunctionBasedOnMobileOs()).collect(Collectors.toList());
  }

  private Function<WebElement, String> getFunctionBasedOnMobileOs() {
    if (AppiumDriverRunner.isAndroidDriver()) {
      return element -> element.getAttribute(androidAttributeName);
    } else if (AppiumDriverRunner.isIosDriver()) {
      return element -> element.getAttribute(androidAttributeName);
    } else {
      throw new IllegalArgumentException("Appium Collection Condition is only applicable for android and ios driver. " +
        "Please use Condition class instead.");
    }
  }
}
