package integration;

import com.codeborne.selenide.*;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


public class LazyEvaluationTest extends IntegrationTest {

  static SelenideElement h1 = $("h1");
  static SelenideElement button = $("#double-clickable-button");

  static SelenideElement input1 = $$("input").first();
  static SelenideElement input2 = $$("input").last();
  static SelenideElement input3 = $$("input").get(2);


  static ElementsCollection inputs1 = $$("input").filterBy(visible);
  static ElementsCollection inputs2 = $$("input").first(2);
  static ElementsCollection inputs3 = $$("input").last(2);



  @Before
  public void openTestPage() {
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
    inputs1.shouldHave(sizeGreaterThanOrEqual(1));
    inputs2.shouldHaveSize(2);
    inputs3.shouldHaveSize(2);
  }


}
