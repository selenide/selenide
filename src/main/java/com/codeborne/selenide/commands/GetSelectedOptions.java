package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.Alias;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.impl.Alias.NONE;

@ParametersAreNonnullByDefault
public class GetSelectedOptions implements Command<ElementsCollection> {
  @Override
  public ElementsCollection execute(SelenideElement proxy, final WebElementSource selectElement, @Nullable Object[] args) {
    return new ElementsCollection(new SelectedOptionsCollection(selectElement));
  }

  private static class SelectedOptionsCollection implements CollectionSource {
    private final WebElementSource selectElement;
    private Alias alias = NONE;

    private SelectedOptionsCollection(WebElementSource selectElement) {
      this.selectElement = selectElement;
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public List<WebElement> getElements() {
      return selectElement.driver().executeJavaScript(
        "return arguments[0].selectedOptions", selectElement.getWebElement()
      );
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public WebElement getElement(int index) {
      return selectElement.driver().executeJavaScript(
        "return arguments[0].selectedOptions[arguments[1]]", selectElement.getWebElement(), index
      );
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public String getSearchCriteria() {
      return selectElement.description() + " selected options";
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public String getSearchLocator() {
      return selectElement.getSearchLocator();
    }

    @Override
    public String toString() {
      return selectElement + " selected options";
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public Driver driver() {
      return selectElement.driver();
    }

    @Nonnull
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
