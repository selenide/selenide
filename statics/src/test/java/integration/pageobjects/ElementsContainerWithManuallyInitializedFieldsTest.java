package integration.pageobjects;

import com.codeborne.selenide.Container;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import java.util.Iterator;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

  @Test
  void listWithFindByAnnotation() {
    MyContainerWithList page = page();
    page.options.get(0).shouldHave(text("-- Select your hero --"));
    page.options.get(1).shouldHave(text("John Mc'Lain"));
  }

  @Test
  void iterableWithFindByAnnotation() {
    MyContainerWithIterable page = page();
    Iterator<SelenideElement> iterator = page.options.iterator();
    iterator.next().shouldHave(text("-- Select your hero --"));
    iterator.next().shouldHave(text("John Mc'Lain"));
  }

  @Test
  void cannotInitializeElementsContainerOutsidePageObject() {
    assertThatThrownBy(() -> page(InvalidContainer.class))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Cannot initialize field InvalidContainer.self: it's not bound to any page object");
  }

  private static class MyPage {
    @FindBy(css = "#apostrophes-and-quotes")
    MyContainer container;
  }

  private static class MyContainer implements Container {
    SelenideElement headerLink = $("h2>a");
    ElementsCollection options = $$("#hero>option");
  }

  private static class MyContainerWithList implements Container {
    @FindBy(css = "#hero>option")
    List<SelenideElement> options;
  }

  private static class MyContainerWithIterable implements Container {
    @FindBy(css = "#hero>option")
    Iterable<SelenideElement> options;
  }

  private static class InvalidContainer implements Container {
    @Self
    SelenideElement self;

    @FindBy(css = "h2 a")
    SelenideElement headerLink;
  }
}
