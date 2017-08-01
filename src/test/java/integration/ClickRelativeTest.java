package integration;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Selenide.$;

public class ClickRelativeTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_relative_click_position.html");
  }


  @Test
  public void userCanClickElementWithOffsetPosition_withActions() {
    Configuration.clickViaJs = false;

    $("#page").click(123, 321);
    $("#coords").should(matchText("(123, 321)"));
  }

  @Test
  public void userCanClickElementWithOffsetPosition_withJavascript() {
    Configuration.clickViaJs = true;
    $("#page").click(321, 123);
    $("#coords").should(matchText("(321, 123)"));
  }
}
