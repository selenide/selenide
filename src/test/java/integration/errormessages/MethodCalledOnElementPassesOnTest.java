package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import integration.IntegrationTest;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static integration.helpers.HTMLBuilderForTestPreconditions.Given;
import static org.junit.Assert.*;

public class MethodCalledOnElementPassesOnTest extends IntegrationTest {

  @Before
  public void openPage() {
    Given.openedPageWithBody(
        "<ul>Hello to:",
        "<li class='the-expanse detective'>Miller <label>detective</label></li>",
        "<li class='the-expanse missing'>Julie Mao</li>",
        "</ul>"
    );
    Configuration.timeout = 0;
  }

  @Test
  public void shouldCondition_When$Element() {
    SelenideElement element = $("ul li");

    element.shouldHave(text("Miller"));
  }

  @Test
  public void actionWithoutWaiting__When$Element() {
    SelenideElement element = $("ul li");

    assertTrue(element.isDisplayed());
  }

  @Test
  public void actionWithoutWaiting_When$Element_WithNonExistentWebElement() {
    SelenideElement element = $("ul .nonexistent");

    assertFalse(element.exists());
  }

  @Test
  public void shouldCondition_WhenCollectionElementByIndex() {
    SelenideElement element = $$("ul li").get(0);

    element.shouldHave(text("Miller"));
  }

  @Test
  public void actionWithVisibilityWaiting_WhenCollectionElementByIndex() {
    SelenideElement element = $$("ul li").get(0);

    element.click();
  }

  @Test
  public void shouldCondition_WhenCollectionElementByCondition() {
    SelenideElement element = $$("li").findBy(cssClass("the-expanse"));

    element.shouldBe(visible);
  }

  @Test
  public void actionWithExistenceWaiting_WhenCollectionElementByCondition() {
    SelenideElement element = $$("li").findBy(cssClass("the-expanse"));

    assertEquals("Miller detective", element.text());
  }

  @Test
  public void shouldCondition_WhenInnerElement() {
    SelenideElement element = $("ul").find(".the-expanse");

    element.shouldBe(visible);
  }

  @Test
  public void actionWithVisibilityWaiting_WhenInnerElement() {
    SelenideElement element = $("ul").find(".the-expanse");

    element.doubleClick();
  }

  /******************************************************
   * More complicated useful options
   * $$.filterBy(condition).findBy(condition).find
   ******************************************************/

  @Test
  public void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).findBy(cssClass("detective")).find("label");

    element.shouldHave(exactText("detective"));
  }

  /******************************************************
   * More complicated useful options
   * $$.filterBy(condition).get(index).find
   ******************************************************/
  @Test
  public void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).get(0).find("label");

    element.shouldHave(exactText("detective"));
  }


}
