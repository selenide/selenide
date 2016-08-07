package integration;

import org.junit.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class InvisibleElementTest extends IntegrationTest {
  @Before
  public void clickHidesElement() {
    openFile("elements_disappear_on_click.html");
    $("#hide").click();
    $("#hide").waitUntil(hidden, 2000);
  }

  @Test
  public void shouldBeHidden() {
    $("#hide").shouldBe(hidden);
  }

  @Test
  public void shouldNotBeVisible() {
    $("#hide").shouldNotBe(visible);
  }

  @Test
  public void shouldNotHaveTextHide() {
    $("#hide").shouldNotHave(text("Hide me").because("Text should disappear"));
  }

  @Test
  public void shouldHaveAttribute() {
    $("#hide").shouldHave(attribute("id", "hide").because("Attributes don't disappear"));
  }

  @Test
  public void shouldHaveCssClasses() {
    $("#hide").shouldHave(cssClass("someclass").because("Attributes don't disappear"));
  }
  @Test
  public void shouldNotHaveTextRemove() {
    $("#hide").shouldNotHave(text("Remove me").because("Text never existed."));
  }

}
