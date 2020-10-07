package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.closeWebDriver;
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

    String cat = Selenide.localStorage().getItem("cat");
    String mouse = Selenide.localStorage().getItem("mouse");

    assertThat(cat).isEqualTo("Tom");
    assertThat(mouse).isEqualTo("Jerry");
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
