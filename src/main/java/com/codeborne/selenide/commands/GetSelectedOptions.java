package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class GetSelectedOptions implements Command<ElementsCollection> {
  @Override
  public ElementsCollection execute(SelenideElement proxy, final WebElementSource selectElement, @Nullable Object[] args) {
    return new ElementsCollection(new WebElementsCollection() {
      @Override
      @CheckReturnValue
      @Nonnull
      public List<WebElement> getElements() {
        return new Select(selectElement.getWebElement()).getAllSelectedOptions();
      }

      @Override
      @CheckReturnValue
      @Nonnull
      public String description() {
        return selectElement.getSearchCriteria() + " selected options";
      }

      @Override
      @CheckReturnValue
      @Nonnull
      public Driver driver() {
        return selectElement.driver();
      }
    });
  }
}
