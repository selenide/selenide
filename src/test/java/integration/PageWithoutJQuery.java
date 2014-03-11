package integration;

import com.codeborne.selenide.JQuery;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertFalse;

public class PageWithoutJQuery extends IntegrationTest {
  JQuery jQuery = new JQuery();

  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void worksForPagesWithoutJQuery() {
    assertFalse(jQuery.isJQueryAvailable());
  }

  @Test
  public void setValueTriggersOnChangeEvent() {
    $("#username").setValue("john");
    $("#username-mirror").shouldHave(text("john"));

    $("#username").append(" ");
    $("#username").append("bon-jovi");

    $("#username-mirror").shouldHave(text("john bon-jovi"));
  }
}
