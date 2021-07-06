package integration;

import com.codeborne.selenide.ex.ConditionNotMetException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.sessionStorage;
import static com.codeborne.selenide.SessionStorageConditions.containItem;
import static com.codeborne.selenide.SessionStorageConditions.containItemWithValue;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class SessionStorageTest extends IntegrationTest {
  @AfterAll
  static void tearDown() {
    closeWebDriver();
  }

  @BeforeEach
  void openTestPage() {
    openFile("session-storage.html");
  }

  @Test
  void setAndGetItem() {
    sessionStorage().setItem("cat", "Tom");
    sessionStorage().setItem("mouse", "Jerry");
    sessionStorage().should(containItem("cat"), "Item 'cat' value doesn't match", ofMillis(10000));
    sessionStorage().should(containItemWithValue("cat", "Tom"), "Item 'cat' value doesn't match", ofMillis(10000));
    sessionStorage().should(containItemWithValue("mouse", "Jerry"), "Item 'mouse' value doesn't match");
  }

  @Test
  void assertPresenceOfItemInSessionStorage() {
    $("#button-put").click();
    sessionStorage().should(containItem("it"), "Button should put item to sessionStorage after 1 second", ofMillis(2000));
    sessionStorage().should(containItemWithValue("it", "works"), "Button should put item to sessionStorage after 1 second", ofMillis(2000));
  }

  @Test
  void assertAbsenceOfItemInSessionStorage() {
    sessionStorage().setItem("it", "is present");
    $("#button-remove").click();
    sessionStorage().shouldNot(containItem("it"), "Button should remove item from sessionStorage after 1 second", ofMillis(2000));
    sessionStorage().shouldNot(containItemWithValue("it", "is present"), "Button should remove item from sessionStorage after 1 second", ofMillis(2000));
  }

  @Test
  void checkValueOfItem() {
    $("#button-put").click();
    sessionStorage().shouldNot(containItemWithValue("it", "another"), "Item has different value", ofMillis(2000));
  }

  @Test
  void errorMessageWhenItemIsMissing() {
    assertThatThrownBy(() ->
      sessionStorage().should(containItem("foo"), "sessionStorage should contain item foo", ofMillis(10))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("sessionStorage should contain item foo")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 10 ms.");
  }

  @Test
  void errorMessageWhenItemHasWrongValue() {
    $("#button-put").click();

    assertThatThrownBy(() ->
      sessionStorage().should(containItemWithValue("it", "wrong"), "sessionStorage should contain item 'it' with value 'wrong'")
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("sessionStorage should contain item 'it' with value 'wrong'")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void removeItem() {
    sessionStorage().setItem("cat", "Tom");
    assertThat(sessionStorage().getItem("cat")).isEqualTo("Tom");

    sessionStorage().removeItem("cat");
    assertThat(sessionStorage().getItem("cat")).isNull();
  }

  @Test
  void clearAndSizeSessionStorage() {
    sessionStorage().setItem("cat", "Tom");
    sessionStorage().setItem("mouse", "Jerry");
    assertThat(sessionStorage().size()).isEqualTo(2);

    sessionStorage().clear();
    assertThat(sessionStorage().size()).isEqualTo(0);
  }
}
