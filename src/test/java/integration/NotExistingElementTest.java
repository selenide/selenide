package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.match;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class NotExistingElementTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("elements_disappear_on_click.html");
  }

  @Test
  void shouldNotExist() {
    $("#not_exist").shouldNot(exist);
  }

  @Test
  void shouldNotExist_because() {
    $("#not_exist").shouldNot(exist.because("it was removed in last release"));
  }

  @Test
  void should_not_exist() {
    $("#not_exist").should(not(exist));
  }

  @Test
  void should_not_exist_because() {
    $("#not_exist").should(not(exist).because("it was removed in last release"));
  }

  @Test
  void shouldNot_be_exist() {
    $("#not_exist").shouldNot(be(exist));
  }

  @Test
  void shouldNot_be_exist_because() {
    $("#not_exist").shouldNot(be(exist).because("it was removed in last release"));
  }

  @Test
  void shouldNot_match() {
    assertThatThrownBy(() ->
      $("#not_exist").shouldNot(match("border=1", el -> el.getAttribute("border").equals("1")))
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#not_exist}");
  }

  @Test
  void shouldNotExistIfParentDoesNotExist() {
    $$("#not_exist").first().$("#multirowTable").shouldNot(exist);
  }

  @Test
  void shouldBeHidden() {
    $("#not_exist").shouldBe(hidden);
  }

  @Test
  void shouldNotBeVisible() {
    $("#not_exist").shouldNotBe(visible);
  }

  @Test
  void toWebElement_shouldNotWait() {
    setTimeout(4000);
    long start = System.nanoTime();
    try {
      assertThatThrownBy(() -> $("#not_exist").toWebElement())
        .isInstanceOf(org.openqa.selenium.NoSuchElementException.class)
        .hasMessageContaining("Unable to locate element:")
        .hasMessageContaining("#not_exist");
    }
    finally {
      long end = System.nanoTime();
      assertThat(TimeUnit.NANOSECONDS.toMillis(end - start)).isLessThan(500);
    }
  }

  @Test
  void getWrappedElement_waits_untilElementApears() {
    setTimeout(1000);
    long start = System.nanoTime();
    try {
      assertThatThrownBy(() -> $("#not_exist").getWrappedElement())
        .isInstanceOf(ElementNotFound.class)
        .hasMessageStartingWith("Element not found {#not_exist}")
        .hasCauseExactlyInstanceOf(org.openqa.selenium.NoSuchElementException.class)
        .getCause()
        .hasMessageContainingAll("Unable to locate element:", "#not_exist");
    }
    finally {
      long end = System.nanoTime();
      assertThat(TimeUnit.NANOSECONDS.toMillis(end - start))
        .isBetween(1000L, 3000L);
    }
  }

  @Test
  void shouldNotHaveText_fails_ifElementIsNotFound() {
    assertThatThrownBy(() ->
      $("#not_exist").shouldNotHave(text("Remove me"))
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#not_exist}");
  }

  @Test
  void shouldNotHaveText_withBecause_fails_ifElementIsNotFound() {
    assertThatThrownBy(() ->
      $("#not_exist").shouldNotHave(text("Remove me").because("it was removed in last release"))
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#not_exist}")
      .hasMessageContaining("because it was removed in last release");
  }

  @Test
  void shouldNotHaveAttribute_fails_ifElementIsNotFound() {
    assertThatThrownBy(() ->
      $("#not_exist").shouldNotHave(attribute("abc"))
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#not_exist}");
  }
}
