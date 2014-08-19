package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * All checks in this class are equivalent
 */
public class ElementDisappearsTest extends IntegrationTest {
  @Before
  public void clickHidesElement() {
    openFile("element_disappears_on_click.html");
    $("button").click();
  }

  @Test
  public void shouldBeHidden() {
    $("button").shouldBe(hidden);
  }

  @Test
  public void shouldDisappear() {
    $("button").should(disappear);
  }

  @Test
  public void waitUntilDisappears() {
    $("button").waitUntil(disappears, 2000);
  }

  @Test
  public void shouldNotBeVisible() {
    $("button").shouldNotBe(visible);
  }

  @Test
  public void shouldNotPresent() {
    $("button").shouldNot(present);
  }

  @Test
  public void shouldNotExist() {
    $("button").shouldNot(exist);
  }

  @Test
  public void shouldNotAppear() {
    $("button").shouldNot(appear);
  }
}
