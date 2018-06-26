package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.SelectorMode.CSS;
import static com.codeborne.selenide.Configuration.SelectorMode.Sizzle;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

class SizzleSelectorsTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_with_jquery.html");
    Configuration.selectorMode = Sizzle;
  }

  @AfterEach
  void tearDown() {
    Configuration.selectorMode = CSS;
  }

  @Test
  void canUseSizzleSelectors() {
    $$(":input").shouldHave(size(4));
    $$(":input:not(.masked)").shouldHave(size(3));
    $$(":header").shouldHave(size(3)); // h1, h1, h2
    $$(":parent").shouldHave(size(13)); // all non-leaf elements
    $$(":not(:parent)").shouldHave(size(13)); // all leaf elements

    // $$("input:visible").shouldHave(size(3));  Unfortunately Sizzle does not support ":visible" :(

    $("input:first").shouldHave(attribute("name", "username"));
    $("input:nth(1)").shouldHave(attribute("name", "password"));
    $("input:last").shouldHave(attribute("id", "double-clickable-button"));
    $("input[name!='username'][name!='password']").shouldHave(attribute("name", "rememberMe"));
    $(":header").shouldHave(text("Page with JQuery"));
    $(":header", 1).shouldHave(text("Now typing"));
    $("label:contains('assword')").shouldHave(text("Password:"));
    Assertions.assertEquals("title", $(":parent:not('html'):not('head')").getTagName());
  }
}
