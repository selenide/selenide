package integration;

import org.junit.Ignore;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SizzleSelectorsTest extends IntegrationTest {
  @Test @Ignore
  public void canUseSizzleSelectorsOnAnyJQueryPage() {
    openFile("page_with_jquery.html");
    $$(":input").shouldHave(size(3));
    $$(":input:not(.masked)").shouldHave(size(2));
    
    $("input:nth(2)").shouldHave(attribute("name", "password"));
  }
}
