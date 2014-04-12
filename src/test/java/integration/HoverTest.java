package integration;

import com.codeborne.selenide.Screenshots;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.actions;

public class HoverTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_jquery.html");
  }

  @Test
  public void canEmulateHover() {
    Screenshots.startContext(getClass().getName(), "canEmulateHover");

    $("#hoverable").shouldHave(text("It's not hover"));
    Screenshots.takeScreenShot("01");

    actions().moveToElement($("#hoverable")).perform();
    $("#hoverable").shouldHave(text("It's hover"));
    Screenshots.takeScreenShot("02");

    actions().moveToElement($("h1")).perform();
    $("#hoverable").shouldHave(text("It's not hover"));
    Screenshots.takeScreenShot("03");

    System.out.println(Screenshots.finishContext());
  }
}
