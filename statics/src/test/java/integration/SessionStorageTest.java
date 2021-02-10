package integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.sessionStorage;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by dbudim on 10.02.2021
 */

public class SessionStorageTest extends IntegrationTest {

  @BeforeEach
  public void openTestPage() {
    openFile("empty.html");
  }

  @Test
  public void setGetItem() {
    sessionStorage().setItem("pied", "piper");
    assertEquals("piper", sessionStorage().getItem("pied"), "there is no item in session storage");
  }

  @Test
  public void getSize() {
    sessionStorage().setItem("pied", "piper");
    sessionStorage().setItem("John", "Wick");
    assertEquals(2, sessionStorage().size(), "session storage size doesn't match");
  }

  @Test
  public void removeItem() {
    sessionStorage().setItem("pied", "piper");
    sessionStorage().setItem("John", "Wick");

    sessionStorage().removeItem("pied");
    assertFalse(sessionStorage().containsItem("pied"), "item wasn't removed fromm session storage");
  }

  @Test
  public void clearTest() {
    sessionStorage().setItem("pied", "piper");
    sessionStorage().setItem("John", "Wick");
    sessionStorage().clear();
    assertTrue(sessionStorage().isEmpty(), "session storage wasn't cleared");
  }

  @AfterAll
  public static void tearDown() {
    closeWebDriver();
  }
}
