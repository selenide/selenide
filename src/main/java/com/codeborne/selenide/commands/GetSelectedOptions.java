package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class GetSelectedOptions implements Command<ElementsCollection> {
  @Override
  public ElementsCollection execute(SelenideElement proxy, final WebElementSource selectElement, Object[] args) {
    return new ElementsCollection(new WebElementsCollection() {
      @Override
      public List<WebElement> getElements() {
        return new Select(selectElement.getWebElement()).getAllSelectedOptions();
      }

      @Override
      public String description() {
        return selectElement.getSearchCriteria() + " selected options";
      }

      @Override
      public Driver driver() {
        return selectElement.driver();
      }
    });
  }
}
