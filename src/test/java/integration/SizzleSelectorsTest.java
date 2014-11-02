package integration;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.SelectorMode.CSS;
import static com.codeborne.selenide.Configuration.SelectorMode.Sizzle;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.Assert.assertEquals;

public class SizzleSelectorsTest extends IntegrationTest {
  @Before
  public void setUp() {
    openFile("page_with_jquery.html");
    Configuration.selectorMode = Sizzle;
  }

  @After
  public void tearDown() {
    Configuration.selectorMode = CSS;
  }

  @Test
  public void canUseSizzleSelectors() {
    $$(":input").shouldHave(size(3));
    $$(":input:not(.masked)").shouldHave(size(2));
    $$(":header").shouldHave(size(3)); // h1, h1, h2
    $$(":parent").shouldHave(size(13)); // all non-leaf elements
    $$(":not(:parent)").shouldHave(size(12)); // all leaf elements
    
    // $$("input:visible").shouldHave(size(3));  Unfortunately Sizzle does not support ":visible" :(

    $("input:first").shouldHave(attribute("name", "username"));
    $("input:nth(1)").shouldHave(attribute("name", "password"));
    $("input:last").shouldHave(attribute("name", "rememberMe"));
    $("input[name!='username'][name!='password']").shouldHave(attribute("name", "rememberMe"));
    $(":header").shouldHave(text("Page with JQuery"));
    $(":header", 1).shouldHave(text("Now typing"));
    $(":contains('not hover')").shouldHave(text("It's not hover"));
    assertEquals("title", $(":parent:not('html'):not('head')").getTagName());
  }
}
