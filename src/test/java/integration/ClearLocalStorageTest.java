package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.clearBrowserLocalStorage;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.Assert.assertEquals;

public class ClearLocalStorageTest extends IntegrationTest {
  @Before
  public void addDataToLocalStorage() {
    open("/start_page.html");
    executeJavaScript("localStorage.setItem('key1', 'item1');");
    executeJavaScript("localStorage.setItem('key2', 'item2');");
  }

  @Test
  public void clearLocalStorageTest() {
    assertEquals(2L, getLocalStorageLength());

    clearBrowserLocalStorage();
    
    assertEquals(0L, getLocalStorageLength());
  }

  private long getLocalStorageLength() {
    return (Long) executeJavaScript("return localStorage.length;");
  }
}
