package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.LocalStorageConditions.containsItemWithValue;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;

final class LocalStorageTest extends IntegrationTest {
  @AfterAll
  static void tearDown() {
    closeWebDriver();
  }

  @BeforeEach
  void openTestPage() {
    openFile("empty.html");
  }

  @Test
  void setAndGetItem() {
    Selenide.localStorage().setItem("cat", "Tom");
    Selenide.localStorage().setItem("mouse", "Jerry");
    Selenide.localStorage().should(containsItemWithValue("cat","Tom"),"Item 'cat' value doesn't match", ofMillis(10000));
    Selenide.localStorage().should(containsItemWithValue("mouse","Jerry"),"Item 'mouse' value doesn't match");
  }

  @Test
  void removeItem() {
    Selenide.localStorage().setItem("cat", "Tom");
    Selenide.localStorage().removeItem("cat");
    String cat = Selenide.localStorage().getItem("cat");
    assertThat(cat).isEqualTo(null);
  }

  @Test
  void clearAndSizeLocalStorage() {
    Selenide.localStorage().setItem("cat", "Tom");
    Selenide.localStorage().setItem("mouse", "Jerry");
    Selenide.localStorage().clear();
    assertThat(Selenide.localStorage().size()).isEqualTo(0);
  }
}
