package com.codeborne.selenide.junit;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.logevents.SoftAssertsErrorsCollector;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static java.util.Objects.requireNonNull;

/**
 * Rule for assert selenide verification softly.
 * <br>
 * Using:
 * 1. Add rule for test class: {@code @Rule public SoftAsserts softAsserts = new SoftAsserts();}
 * <br>
 * 2. Configure selenide to assert softly: {@code Configuration.assertionMode = SOFT;}
 */
@ParametersAreNonnullByDefault
public class SoftAsserts extends ExternalResource {
  private Description currentTest;
  private final boolean fullStacktraces;

  public SoftAsserts() {
    this(true);
  }

  public SoftAsserts(boolean fullStacktraces) {
    this.fullStacktraces = fullStacktraces;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    currentTest = description;
    return super.apply(base, description);
  }

  @Override
  protected void before() {
    SelenideLogger.addListener(LISTENER_SOFT_ASSERT, new SoftAssertsErrorsCollector());
  }

  @Override
  protected void after() {
    ErrorsCollector errorsCollector = requireNonNull(SelenideLogger.removeListener(LISTENER_SOFT_ASSERT));

    // if both test and "after" method threw an error, JUnit4 collects all of them
    // and throws org.junit.internal.runners.model.MultipleFailureException
    errorsCollector.cleanAndThrowAssertionError(currentTest.getDisplayName(), null, fullStacktraces);
  }
}
