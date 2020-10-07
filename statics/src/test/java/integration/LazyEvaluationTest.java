package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

final class LazyEvaluationTest extends IntegrationTest {

  private SelenideElement h1 = $("h1");
  private SelenideElement button = $("#some-button");
  private SelenideElement input1 = $$("input").first();
  private SelenideElement input2 = $$("input").last();
  private SelenideElement input3 = $$("input").get(2);
  private ElementsCollection inputs1 = $$("input").filterBy(visible);
  private ElementsCollection inputs2 = $$("input").first(2);
  private ElementsCollection inputs3 = $$("input").last(2);

  @BeforeAll
  static void guaranteeThatBrowserIsNotOpenedTooEarly() {
    Configuration.browser = "do not even try to open browser too early!";
    closeWebDriver();
  }

  @BeforeEach
  void openTestPage() {
    openFile("page_with_jquery.html");
  }

  @Test
  void singleElementLazyFound() {
    h1.shouldBe(visible);
    button.click();
  }

  @Test
  void singleElementFromCollectionLazyFound() {
    input1.shouldBe(visible);
    input2.shouldBe(visible);
    input3.shouldBe(visible);
  }

  @Test
  void collectionLazyFound() {
    inputs1.shouldHave(size(4));
    inputs2.shouldHave(size(2));
    inputs3.shouldHave(size(2));
  }
}
