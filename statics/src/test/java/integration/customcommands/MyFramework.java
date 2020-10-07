package integration.customcommands;

import com.codeborne.selenide.commands.Commands;
import com.codeborne.selenide.impl.ElementFinder;
import org.openqa.selenium.By;

import java.util.concurrent.atomic.AtomicInteger;

import static com.codeborne.selenide.WebDriverRunner.driver;

final class MyFramework {
  static AtomicInteger tripleClickCounter = new AtomicInteger();
  static AtomicInteger quadrupleClickCounter = new AtomicInteger();

  public static void setUp() {
    Commands.getInstance().add("tripleClick", new TripleClick());
    Commands.getInstance().add("quadrupleClick", new QuadrupleClick());
  }

  /**
   * Replacement for standard Selenide `$` method.
   *
   * @param selector CSS selector
   *
   * @return MySelenideElement - an "advanced" version of `SelenideElement` with more custom methods
   */
  public static MySelenideElement $_(String selector) {
    return ElementFinder.wrap(driver(), MySelenideElement.class, null, By.cssSelector(selector), 0);
  }
}
