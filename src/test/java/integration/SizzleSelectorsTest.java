package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.SelectorMode.Sizzle;
import static org.assertj.core.api.Assertions.assertThat;

final class SizzleSelectorsTest extends BaseIntegrationTest {
  SelenideDriver driver = new SelenideDriver(new SelenideConfig().baseUrl(getBaseUrl()).selectorMode(Sizzle));

  @AfterEach
  void tearDown() {
    driver.close();
  }

  @Test
  void canUseSizzleSelectors() {
    driver.open("/page_with_jquery.html");
    driver.$$(":input").shouldHave(size(4));
    driver.$$(":input:not(.masked)").shouldHave(size(3));
    driver.$$(":header").shouldHave(size(3)); // h1, h1, h2
    driver.$$(":parent").shouldHave(sizeGreaterThanOrEqual(13)); // all non-leaf elements
    driver.$$(":not(:parent)").shouldHave(size(14)); // all leaf elements

    driver.$("input:first").shouldHave(attribute("name", "username"));
    driver.$("input:nth(1)").shouldHave(attribute("name", "password"));
    driver.$("input:last").shouldHave(attribute("id", "some-button"));
    driver.$("input[name!='username'][name!='password']").shouldHave(attribute("name", "rememberMe"));
    driver.$(":header").shouldHave(text("Page with JQuery"));
    driver.$(":header", 1).shouldHave(text("Now typing"));
    driver.$("label:contains('assword')").shouldHave(text("Password:"));
    assertThat(driver.$(":parent:not('html'):not('head')").getTagName()).isEqualTo("title");
  }

  @Test
  void canUseSizzleSelectors_onTodoList_dojo() {
    driver.open("https://todomvc.com/examples/dojo/");
    driver.$$(":header").shouldHave(size(6));

    driver.$$(":input").shouldHave(sizeGreaterThanOrEqual(3));
    driver.$$(":input:not(.masked)").shouldHave(sizeGreaterThanOrEqual(3));
    driver.$$(":header").shouldHave(sizeGreaterThanOrEqual(6)); // h1, h1, h2
    driver.$$(":parent").shouldHave(sizeGreaterThanOrEqual(13)); // all non-leaf elements
    driver.$$(":not(:parent)").shouldHave(sizeGreaterThanOrEqual(21)); // all leaf elements

    driver.$("input:first").shouldHave(attribute("class", "new-todo"));
    driver.$("input:nth(1)").shouldHave(attribute("class", "toggle-all"));
    driver.$("input:last").should(exist);
    driver.$("input[name!='username'][name!='password']")
      .should(exist)
      .shouldNotHave(attribute("name", "username"), attribute("name", "password"));
    driver.$(":header").shouldHave(text("Dojo"));
    driver.$(":header", 1).shouldHave(text("Example"));
    driver.$("label:contains('assword')").shouldNot(exist);
    assertThat(driver.$(":parent:not('html'):not('head')").getTagName()).isEqualTo("title");
  }

  @Test
  void canUseSizzleSelectors_onTodoList_jquery() {
    driver.open("https://todomvc4tasj.herokuapp.com/");
    driver.$$(":header").shouldHave(sizeGreaterThanOrEqual(1));
  }

  @Test
  void canUseSizzleSelectors_onTodoList_troopjs() {
    driver.open("https://todomvc.com/examples/troopjs_require//");
    driver.$$(":header").shouldHave(sizeGreaterThanOrEqual(1));
  }
}
