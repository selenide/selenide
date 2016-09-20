package integration;

import org.junit.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ElementHiddenTest extends IntegrationTest {
  @Before
  public void clickRemovesElement() {
    openFile("elements_disappear_on_click.html");
    $("#hide").click();
  }

  @Test
  public void shouldBeHidden() {
    $("#hide").shouldBe(hidden);
  }

  @Test
  public void shouldDisappear() {
    $("#hide").should(disappear);
  }

  @Test
  public void waitUntilDisappears() {
    $("#hide").waitUntil(disappears, 2000);
  }

  @Test
  public void shouldNotBeVisible() {
    $("#hide").shouldNotBe(visible);
  }

  @Test
  public void shouldBePresent() {
    $("#hide").shouldBe(present);
  }

  @Test
  public void shouldExist() {
    $("#hide").should(exist);
  }

  @Test
  public void shouldNotAppear() {
    $("#hide").shouldNot(appear);
  }

}
