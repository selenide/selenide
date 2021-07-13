package integration;

import com.codeborne.selenide.ex.ConditionMetException;
import com.codeborne.selenide.ex.ConditionNotMetException;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sessionStorage;
import static com.codeborne.selenide.SessionStorageConditions.item;
import static com.codeborne.selenide.SessionStorageConditions.itemWithValue;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class SessionStorageTest extends IntegrationTest {
  @AfterAll
  static void resetSessionStorage() {
    sessionStorage().clear();
  }

  @BeforeEach
  void openTestPage() {
    openFile("session-storage.html");
  }

  @Test
  void setAndGetItem() {
    sessionStorage().setItem("cat", "Tom");
    sessionStorage().setItem("mouse", "Jerry");
    sessionStorage().shouldHave(item("cat"), ofMillis(10000));
    sessionStorage().shouldHave(itemWithValue("cat", "Tom"), ofMillis(10000));
    sessionStorage().shouldHave(itemWithValue("mouse", "Jerry"));
  }

  @Test
  void getAllItems() {
    sessionStorage().setItem("cat", "Tom");
    sessionStorage().setItem("mouse", "Jerry");
    assertThat(sessionStorage().getItems()).containsAllEntriesOf(ImmutableMap.of("cat", "Tom", "mouse", "Jerry"));
  }

  @Test
  void assertPresenceOfItemInSessionStorage() {
    $("#button-put").click();
    sessionStorage().shouldHave(item("it"), ofMillis(2000));
    sessionStorage().shouldHave(itemWithValue("it", "works"), ofMillis(2000));
  }

  @Test
  void assertAbsenceOfItemInSessionStorage() {
    sessionStorage().setItem("it", "is present");
    $("#button-remove").click();
    sessionStorage().shouldNotHave(item("it"), ofMillis(2000));
    sessionStorage().shouldNotHave(itemWithValue("it", "is present"), ofMillis(2000));
  }

  @Test
  void checkValueOfItem() {
    $("#button-put").click();
    sessionStorage().shouldNotHave(itemWithValue("it", "another"), ofMillis(2000));
  }

  @Test
  void errorMessageWhenItemIsMissing() {
    assertThatThrownBy(() ->
      sessionStorage().shouldHave(item("foo"), ofMillis(10))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("sessionStorage should have item 'foo'")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 10 ms.");
  }

  @Test
  void errorMessageWhenItemHasWrongValue() {
    $("#button-put").click();

    assertThatThrownBy(() ->
      sessionStorage().shouldHave(itemWithValue("it", "wrong"))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("sessionStorage should have item 'it' with value 'wrong'")
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


  @Test
  void errorMessageWhenItemShouldNotExist() {
    sessionStorage().setItem("cat", "Tom");

    assertThatThrownBy(() ->
      sessionStorage().shouldNotHave(item("cat"))
    )
      .isInstanceOf(ConditionMetException.class)
      .hasMessageStartingWith("sessionStorage should not have item 'cat'")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void errorMessageWhenItemShouldNotHaveGivenValue() {
    sessionStorage().setItem("cat", "Tom");

    assertThatThrownBy(() ->
      sessionStorage().shouldNotHave(itemWithValue("cat", "Tom"))
    )
      .isInstanceOf(ConditionMetException.class)
      .hasMessageStartingWith("sessionStorage should not have item 'cat' with value 'Tom'")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1 ms.");
  }
}
