package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static java.time.Duration.ZERO;

@ParametersAreNonnullByDefault
public class Matches implements Command<Boolean> {
  private static final Logger log = LoggerFactory.getLogger(Matches.class);

  @Override
  @CheckReturnValue
  public Boolean execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    Arguments arguments = new Arguments(args);
    WebElementCondition condition = arguments.nth(0);
    Duration timeout = arguments.ofType(Duration.class).orElse(ZERO);

    if (timeout.toMillis() == 0) {
      return evaluate(locator, condition);
    }
    else {
      return evaluateWithTimeout(locator, condition, timeout.toMillis(), locator.driver().config().pollingInterval());
    }
  }

  @CheckReturnValue
  private boolean evaluate(WebElementSource locator, WebElementCondition condition) {
    WebElement element = getElementOrNull(locator);
    if (element != null) {
      return condition.check(locator.driver(), element).verdict() == ACCEPT;
    }

    return condition.missingElementSatisfiesCondition();
  }

  @CheckReturnValue
  private boolean evaluateWithTimeout(WebElementSource locator, WebElementCondition condition, long timeout, long pollingInterval) {
    Stopwatch stopwatch = new Stopwatch(timeout);
    while (!stopwatch.isTimeoutReached()) {
      boolean result = evaluate(locator, condition);
      if (result) return true;
      stopwatch.sleep(pollingInterval);
    }
    return false;
  }

  @CheckReturnValue
  @Nullable
  protected WebElement getElementOrNull(WebElementSource locator) {
    try {
      return locator.getWebElement();
    }
    catch (WebDriverException | ElementNotFound elementNotFound) {
      log.debug(Cleanup.of.webdriverExceptionMessage(elementNotFound));

      if (Cleanup.of.isInvalidSelectorError(elementNotFound))
        throw Cleanup.of.wrapInvalidSelectorException(elementNotFound);
      return null;
    }
    catch (IndexOutOfBoundsException e) {
      log.debug(Cleanup.of.webdriverExceptionMessage(e));
      return null;
    }
  }
}
