package integration;

import com.codeborne.selenide.ex.ConditionNotMetException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.LocalStorageConditions.item;
import static com.codeborne.selenide.LocalStorageConditions.itemWithValue;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.localStorage;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class LocalStorageTest extends IntegrationTest {
  @AfterAll
  static void resetSessionStorage() {
    localStorage().clear();
  }

  @BeforeEach
  void openTestPage() {
    openFile("local-storage.html");
  }

  @Test
  void setAndGetItem() {
    localStorage().setItem("cat", "Tom");
    localStorage().setItem("mouse", "Jerry");
    localStorage().shouldHave(item("cat"), "Item 'cat' value doesn't match", ofMillis(10000));
    localStorage().shouldHave(itemWithValue("cat", "Tom"), "Item 'cat' value doesn't match", ofMillis(10000));
    localStorage().shouldHave(itemWithValue("mouse", "Jerry"), "Item 'mouse' value doesn't match");
  }

  @Test
  void canChainShouldMethods() {
    localStorage().setItem("cat", "Tom");
    localStorage().setItem("mouse", "Jerry");
    localStorage()
      .shouldHave(item("cat"), "Item 'cat' value doesn't match")
      .shouldHave(item("mouse"), "Item 'mouse' value doesn't match")
      .shouldHave(itemWithValue("cat", "Tom"), "Item 'cat' value doesn't match", ofMillis(10))
      .shouldHave(itemWithValue("mouse", "Jerry"), "Item 'mouse' value doesn't match", ofMillis(20))
      .shouldNotHave(item("dog"), "no dogs")
      .shouldNotHave(itemWithValue("dog", "barks"), "no barking dogs");
  }

  @Test
  void assertPresenceOfItemInLocalStorage() {
    $("#button-put").click();
    localStorage().shouldHave(item("it"), "Button should put item to localStorage after 1 second", ofMillis(2000));
    localStorage().shouldHave(itemWithValue("it", "works"), "Button should put item to localStorage after 1 second", ofMillis(2000));
  }

  @Test
  void assertAbsenceOfItemInLocalStorage() {
    localStorage().setItem("it", "is present");
    $("#button-remove").click();
    localStorage().shouldHave(item("it"), "Button should remove item from localStorage after 1 second", ofMillis(2000));
    localStorage().shouldHave(itemWithValue("it", "is present"), "Button should remove item from localStorage after 1 second", ofMillis(2000));
  }

  @Test
  void checkValueOfItem() {
    $("#button-put").click();
    localStorage().shouldNotHave(itemWithValue("it", "another"), "Item has different value", ofMillis(2000));
  }

  @Test
  void errorMessageWhenItemIsMissing() {
    assertThatThrownBy(() ->
      localStorage().shouldHave(item("foo"), "localStorage should contain item foo", ofMillis(10))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("localStorage should contain item foo")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 10 ms.");
  }

  @Test
  void errorMessageWhenItemHasWrongValue() {
    $("#button-put").click();

    assertThatThrownBy(() ->
      localStorage().shouldHave(itemWithValue("it", "wrong"), "localStorage should contain item 'it' with value 'wrong'")
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("localStorage should contain item 'it' with value 'wrong'")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void removeItem() {
    localStorage().setItem("cat", "Tom");
    assertThat(localStorage().getItem("cat")).isEqualTo("Tom");

    localStorage().removeItem("cat");
    assertThat(localStorage().getItem("cat")).isNull();
  }

  @Test
  void clearAndSizeLocalStorage() {
    localStorage().setItem("cat", "Tom");
    localStorage().setItem("mouse", "Jerry");
    assertThat(localStorage().size()).isEqualTo(2);

    localStorage().clear();
    assertThat(localStorage().size()).isEqualTo(0);
  }
}
