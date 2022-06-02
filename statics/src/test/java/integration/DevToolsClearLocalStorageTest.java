package integration;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class DevToolsClearLocalStorageTest extends IntegrationTest {

  @BeforeEach
  void addDataToLocalStorage() {
    assumeThat(isChrome() || isEdge()).isTrue();
    open("/start_page.html");
    executeJavaScript("localStorage.setItem('key1', 'item1');");
    executeJavaScript("localStorage.setItem('key2', 'item2');");
    Assumptions.assumeFalse(getLocalStorageLength() == 2L);
  }

  @Test
  void clearLocalStorageTest() {
    open("https://www.github.com/");
    Selenide.devTools().clearLocalStorage();
    open("/start_page.html");

    assertThat(getLocalStorageLength()).isEqualTo(0L);
  }

  private long getLocalStorageLength() {
    return (Long) executeJavaScript("return localStorage.length;");
  }
}
