package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * All checks in this class are equivalent
 */
public class ElementRemovedTest extends IntegrationTest {
  @Before
  public void clickRemovesElement() {
	  openFile("elements_disappear_on_click.html");
	  $("#remove").click();
  }

  @Test
  public void shouldBeHidden() {
	  $("#remove").shouldBe(hidden);
  }

  @Test
  public void shouldDisappear() {
	  $("#remove").should(disappear);
  }

  @Test
  public void waitUntilDisappears() {
	  $("#remove").waitUntil(disappears, 2000);
  }

  @Test
  public void shouldNotBeVisible() {
	  $("#remove").shouldNotBe(visible);
  }

  @Test
  public void shouldNotPresent() {
	  $("#remove").shouldNot(present);
  }

  @Test
  public void shouldNotExist() {
	  $("#remove").shouldNot(exist);
  }

  @Test
  public void shouldNotAppear() {
	  $("#remove").shouldNot(appear);
  }

}
