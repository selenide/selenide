package integration.pageobjects;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;

final class ElementsContainerWithManuallyInitializedFieldsTest extends IntegrationTest {

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canInitializeElementsContainerFieldsWithoutFindByAnnotation() {
    MyPage page = page();

    page.container.headerLink.shouldHave(text("Options with 'apostrophes' and \"quotes\""), visible);
    page.container.options.shouldHave(texts("-- Select your hero --", "John Mc'Lain", "Arnold \"Schwarzenegger\"",
      "Mickey \"Rock'n'Roll\" Rourke", "Denzel Washington"));
    page.container.options.first(2).shouldHave(texts("-- Select your hero --", "John Mc'Lain"));
    page.container.options.last(3).shouldHave(texts("Arnold \"Schwarzenegger\"",
      "Mickey \"Rock'n'Roll\" Rourke", "Denzel Washington"));
  }

  private static class MyPage {
    @FindBy(css = "#apostrophes-and-quotes")
    MyContainer container;
  }

  private static class MyContainer implements Container {
    SelenideElement headerLink = $("h2>a");
    ElementsCollection options = $$("#hero>option");
  }
}
