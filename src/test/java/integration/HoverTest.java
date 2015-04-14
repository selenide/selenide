package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class HoverTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_jquery.html");
  }

  @Test
  public void canEmulateHover() {
    $("#hoverable").hover().shouldHave(text("It's hover"));

    $("h1").hover();
    $("#hoverable").shouldHave(text("It's not hover"));
  }
}
