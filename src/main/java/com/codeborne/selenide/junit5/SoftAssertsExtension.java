package com.codeborne.selenide.junit5;

import com.codeborne.selenide.logevents.ErrorsCollector;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static com.codeborne.selenide.logevents.SelenideLogger.addListener;
import static com.codeborne.selenide.logevents.SelenideLogger.removeListener;
import static java.util.Objects.requireNonNull;

/**
 * By using this extension selenide will collect all failed asserts
 * from "should*" and "waitUntil" methods and throw after test finished.
 *
 * <br>
 * To use extension extend you test class with it:
 * <br>
 * {@code @ExtendWith({SoftAssertsExtension.class}}
 * <br>
 * Or register extension in test class:
 * <br>
 * {@code @RegisterExtension static SoftAssertsExtension softAsserts = new SoftAssertsExtension();}
 * <br>
 *
 * @author Aliaksandr Rasolka
 * @since 4.12.2
 */
@ParametersAreNonnullByDefault
public class SoftAssertsExtension implements BeforeEachCallback, AfterEachCallback {
  @Override
  public void beforeEach(final ExtensionContext context) {
    addListener(LISTENER_SOFT_ASSERT, new ErrorsCollector());
  }

  @Override
  public void afterEach(final ExtensionContext context) {
    ErrorsCollector errorsCollector = requireNonNull(removeListener(LISTENER_SOFT_ASSERT));
    errorsCollector.failIfErrors(context.getDisplayName());
  }
}
