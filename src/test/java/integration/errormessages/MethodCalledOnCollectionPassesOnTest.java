package integration.errormessages;

import java.util.Arrays;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import integration.IntegrationTest;
import integration.helpers.HTMLBuilderForTestPreconditions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MethodCalledOnCollectionPassesOnTest extends IntegrationTest {

  @BeforeEach
  void openPage() {
    HTMLBuilderForTestPreconditions.Given.openedPageWithBody(
      "<ul>Hello to:",
      "<li class='the-expanse detective'>Miller</li>",
      "<li class='the-expanse missing'>Julie Mao</li>",
      "</ul>"
    );
    Configuration.timeout = 0;
  }

  @Test
  void shouldCondition_When$$Collection() {
    ElementsCollection collection = $$("ul li");

    collection.shouldHave(exactTexts("Miller", "Julie Mao"));
  }

  @Test
  void actionWithoutWaiting__When$$Collection() {
    ElementsCollection collection = $$("ul li");

    assertThat(Arrays.toString(collection.getTexts()), is("[Miller, Julie Mao]"));
  }

  @Test
  void actionWithoutWaiting_When$$Collection_WithNonExistentWebElements() {
    ElementsCollection collection = $$("ul .nonexistent");

    Assertions.assertEquals(true, collection.isEmpty());
        /*
            there is no exceptions - when collection.isEmpty();
        */
  }

  @Test
  void actionWithoutWaiting_WhenInnerCollection_WithNonExistentInnerWebElements() {
    ElementsCollection collection = $("ul").findAll(".nonexistent");

    Assertions.assertEquals(0, collection.size());
        /*
            there is no exceptions - when collection.isEmpty();
        */
  }

  @Test
  void shouldCondition_WhenFilteredCollection() {
    ElementsCollection collection = $$("ul li").filter(cssClass("the-expanse"));

    collection.shouldHave(exactTexts("Miller", "Julie Mao"));
  }

  @Test
  void shouldCondition_WhenInnerCollection() {
    ElementsCollection collection = $("ul").findAll("li");

    collection.shouldHave(exactTexts("Miller", "Julie Mao"));
  }
}
