package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class PageWithoutJQuery extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void setValueShouldNotTriggerOnChangeEvent() {
    $("#username").setValue("john");
    $("#username-mirror").shouldNotHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("#username-mirror").shouldNotHave(text("john bon-jovi"));
  }
}
