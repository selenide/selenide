package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.Alias;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.impl.Alias.NONE;
import static java.util.Objects.requireNonNull;

public class GetSelectedOptions implements Command<ElementsCollection> {
  @Override
  public ElementsCollection execute(SelenideElement proxy, final WebElementSource selectElement, Object @Nullable [] args) {
    return new ElementsCollection(new SelectedOptionsCollection(selectElement));
  }

  private static class SelectedOptionsCollection implements CollectionSource {
    private final WebElementSource selectElement;
    private Alias alias = NONE;

    private SelectedOptionsCollection(WebElementSource selectElement) {
      this.selectElement = selectElement;
    }

    @Override
    public List<WebElement> getElements() {
      return requireNonNull(selectElement.driver().executeJavaScript(
        "return arguments[0].selectedOptions", selectElement.getWebElement()
      ));
    }

    @Override
    public WebElement getElement(int index) {
      return requireNonNull(selectElement.driver().executeJavaScript(
        "return arguments[0].selectedOptions[arguments[1]]", selectElement.getWebElement(), index
      ));
    }

    @Override
    public String getSearchCriteria() {
      return selectElement.description() + " selected options";
    }

    @Override
    public String toString() {
      return selectElement + " selected options";
    }

    @Override
    public Driver driver() {
      return selectElement.driver();
    }

    @Override
    public Alias getAlias() {
      return alias;
    }

    @Override
    public void setAlias(String alias) {
      this.alias = new Alias(alias);
    }
  }
}
