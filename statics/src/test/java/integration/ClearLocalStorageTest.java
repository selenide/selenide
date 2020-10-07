package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.clearBrowserLocalStorage;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

final class ClearLocalStorageTest extends IntegrationTest {
  @BeforeEach
  void addDataToLocalStorage() {
    open("/start_page.html");
    executeJavaScript("localStorage.setItem('key1', 'item1');");
    executeJavaScript("localStorage.setItem('key2', 'item2');");
  }

  @Test
  void clearLocalStorageTest() {
    assertThat(getLocalStorageLength()).isEqualTo(2L);

    clearBrowserLocalStorage();

    assertThat(getLocalStorageLength()).isEqualTo(0L);
  }

  private long getLocalStorageLength() {
    return (Long) executeJavaScript("return localStorage.length;");
  }
}
