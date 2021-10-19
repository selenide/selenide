package integration;

import com.codeborne.selenide.LocalStorage;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.ex.ConditionMetException;
import com.codeborne.selenide.ex.ConditionNotMetException;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.LocalStorageConditions.item;
import static com.codeborne.selenide.LocalStorageConditions.itemWithValue;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.localStorage;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class LocalStorageTest extends IntegrationTest {
  @AfterAll
  static void resetLocalStorage() {
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
    localStorage().shouldHave(item("cat"), ofMillis(10000));
    localStorage().shouldHave(itemWithValue("cat", "Tom"), ofMillis(10000));
    localStorage().shouldHave(itemWithValue("mouse", "Jerry"));
  }

  @Test
  void getAllItems() {
    localStorage().setItem("cat", "Tom");
    localStorage().setItem("mouse", "Jerry");
    assertThat(localStorage().getItems()).containsAllEntriesOf(ImmutableMap.of("cat", "Tom", "mouse", "Jerry"));
  }

  @Test
  void canChainShouldMethods() {
    localStorage().setItem("cat", "Tom");
    localStorage().setItem("mouse", "Jerry");
    localStorage()
      .shouldHave(item("cat"))
      .shouldHave(item("mouse"))
      .shouldHave(itemWithValue("cat", "Tom"), ofMillis(10))
      .shouldHave(itemWithValue("mouse", "Jerry"), ofMillis(20))
      .shouldNotHave(item("dog"))
      .shouldNotHave(itemWithValue("dog", "barks"));
  }

  @Test
  void assertPresenceOfItemInLocalStorage() {
    $("#button-put").click();
    localStorage().shouldHave(item("it"), ofMillis(2000));
    localStorage().shouldHave(itemWithValue("it", "works"), ofMillis(2000));
  }

  @Test
  void assertAbsenceOfItemInLocalStorage() {
    localStorage().setItem("it", "is present");
    $("#button-remove").click();
    localStorage().shouldHave(item("it"), ofMillis(2000));
    localStorage().shouldHave(itemWithValue("it", "is present"), ofMillis(2000));
  }

  @Test
  void checkValueOfItem() {
    $("#button-put").click();
    localStorage().shouldNotHave(itemWithValue("it", "another"), ofMillis(2000));
  }

  @Test
  void errorMessageWhenItemIsMissing() {
    assertThatThrownBy(() ->
      localStorage().shouldHave(item("foo"), ofMillis(10))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("localStorage should have item 'foo'")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 10 ms.");
  }

  @Test
  void errorMessageWhenItemHasWrongValue() {
    $("#button-put").click();

    assertThatThrownBy(() ->
      localStorage().shouldHave(itemWithValue("it", "wrong"))
    )
      .isInstanceOf(ConditionNotMetException.class)
      .hasMessageStartingWith("localStorage should have item 'it' with value 'wrong'")
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

  @Test
  void errorMessageWhenItemShouldNotExist() {
    localStorage().setItem("cat", "Tom");

    assertThatThrownBy(() ->
      localStorage().shouldNotHave(item("cat"))
    )
      .isInstanceOf(ConditionMetException.class)
      .hasMessageStartingWith("localStorage should not have item 'cat'")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void errorMessageWhenItemShouldNotHaveGivenValue() {
    localStorage().setItem("cat", "Tom");

    assertThatThrownBy(() ->
      localStorage().shouldNotHave(itemWithValue("cat", "Tom"))
    )
      .isInstanceOf(ConditionMetException.class)
      .hasMessageStartingWith("localStorage should not have item 'cat' with value 'Tom'")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void userCanDefineCustomConditions() {
    localStorage().setItem("item1", "foo bar");
    localStorage().setItem("item2", "bar foo");
    localStorage().setItem("item2", "bar foo zoo");
    localStorage().shouldHave(allItemsContaining("foo"));
  }

  @ParametersAreNonnullByDefault
  private ObjectCondition<LocalStorage> allItemsContaining(String expectedValue) {
    return new ObjectCondition<LocalStorage>() {
      @Nonnull
      @Override
      public String description() {
        return String.format("should have all items containing '%s'", expectedValue);
      }

      @Nonnull
      @Override
      public String negativeDescription() {
        return String.format("should not have all items containing '%s'", expectedValue);
      }

      @CheckReturnValue
      @Override
      public boolean test(LocalStorage localStorage) {
        return localStorage.getItems().values().stream()
          .allMatch(value -> value.contains(expectedValue));
      }

      @Nonnull
      @CheckReturnValue
      @Override
      public String actualValue(LocalStorage localStorage) {
        return localStorage.getItems().toString();
      }

      @Override
      @CheckReturnValue
      public String expectedValue() {
        return expectedValue;
      }

      @Nonnull
      @Override
      public String describe(LocalStorage object) {
        return "localStorage";
      }
    };
  }
}
