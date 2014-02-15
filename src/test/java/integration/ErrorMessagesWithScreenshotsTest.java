package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ErrorMessagesWithScreenshotsTest {
  @Before
  public final void setTimeout() {
    timeout = 0;
    open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
  }

  @After
  public final void restoreTimeout() {
    timeout = 4000;
  }

  @Test
  public void parentNotFound() {
    try {
      $("#nonexisting-form")
          .findAll(byText("mymail@gmail.com"))
          .find(cssClass("trash"))
          .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      assertTrue(e.getMessage().contains("Element not found {By.selector: #nonexisting-form}"));
    }
  }

  @Test @Ignore // Issue #61. TODO Fix the code
  public void itShouldBeReportedWhichParentElementIsNotFound() {
    try {
      $("#multirowTable")
          .findAll("thead")
          .findBy(text("mymail@gmail.com"))
          .find(".trash")
          .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      assertTrue(e.getMessage().contains("Element not found {By.selector: thead}"));
    }
  }

  @Test @Ignore // Issue #61. TODO Fix the code
  public void elementNotFoundInsideParent() {
    try {
      $("#multirowTable")
          .findAll("tbody tr")
          .findBy(text("Norris"))
          .find(".first_row")
          .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      assertTrue(e.getMessage().contains("Element not found {By.selector: .first_row}"));
    }
  }
}
