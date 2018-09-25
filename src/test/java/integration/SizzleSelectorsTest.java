package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.SelectorMode.Sizzle;

class SizzleSelectorsTest extends BaseIntegrationTest {
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
    driver.$$(":parent").shouldHave(size(13)); // all non-leaf elements
    driver.$$(":not(:parent)").shouldHave(size(13)); // all leaf elements

    driver.$("input:first").shouldHave(attribute("name", "username"));
    driver.$("input:nth(1)").shouldHave(attribute("name", "password"));
    driver.$("input:last").shouldHave(attribute("id", "some-button"));
    driver.$("input[name!='username'][name!='password']").shouldHave(attribute("name", "rememberMe"));
    driver.$(":header").shouldHave(text("Page with JQuery"));
    driver.$(":header", 1).shouldHave(text("Now typing"));
    driver.$("label:contains('assword')").shouldHave(text("Password:"));
    assertThat(driver.$(":parent:not('html'):not('head')").getTagName()).isEqualTo("title");
  }
}
