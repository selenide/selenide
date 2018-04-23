package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

public class LazyEvaluationTest extends IntegrationTest {

  @BeforeClass
  public static void guaranteeThatBrowserIsNotOpenedTooEarly() {
    Configuration.browser = "do not even try to open browser too early!";
    closeWebDriver();
  }

  SelenideElement h1 = $("h1");
  SelenideElement button = $("#double-clickable-button");

  SelenideElement input1 = $$("input").first();
  SelenideElement input2 = $$("input").last();
  SelenideElement input3 = $$("input").get(2);

  ElementsCollection inputs1 = $$("input").filterBy(visible);
  ElementsCollection inputs2 = $$("input").first(2);
  ElementsCollection inputs3 = $$("input").last(2);

  @Before
  public void openTestPage() {
    Configuration.browser = System.getProperty("selenide.browser");
    openFile("page_with_jquery.html");
  }

  @Test
  public void singleElementLazyFound() {
    h1.shouldBe(visible);
    button.click();
  }

  @Test
  public void singleElementFromCollectionLazyFound() {
    input1.shouldBe(visible);
    input2.shouldBe(visible);
    input3.shouldBe(visible);
  }

  @Test
  public void collectionLazyFound() {
    inputs1.shouldHave(size(4));
    inputs2.shouldHave(size(2));
    inputs3.shouldHave(size(2));
  }
}
