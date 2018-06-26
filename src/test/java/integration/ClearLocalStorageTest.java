package integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.clearBrowserLocalStorage;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;

class ClearLocalStorageTest extends IntegrationTest {
  @BeforeEach
  void addDataToLocalStorage() {
    open("/start_page.html");
    executeJavaScript("localStorage.setItem('key1', 'item1');");
    executeJavaScript("localStorage.setItem('key2', 'item2');");
  }

  @Test
  void clearLocalStorageTest() {
    Assertions.assertEquals(2L, getLocalStorageLength());

    clearBrowserLocalStorage();

    Assertions.assertEquals(0L, getLocalStorageLength());
  }

  private long getLocalStorageLength() {
    return (Long) executeJavaScript("return localStorage.length;");
  }
}
