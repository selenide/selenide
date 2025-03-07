package com.codeborne.selenide.junit5;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SoftAssertsErrorsCollector;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static com.codeborne.selenide.logevents.SelenideLogger.addListener;
import static com.codeborne.selenide.logevents.SelenideLogger.removeListener;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

/**
 * By using this extension selenide will collect all failed asserts
 * from "should*" methods and throw after test finished.
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
 */
public class SoftAssertsExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {
  private static final ExtensionContext.Namespace namespace = create(SoftAssertsExtension.class);

  private final boolean fullStacktraces;

  public SoftAssertsExtension() {
    this(true);
  }

  public SoftAssertsExtension(boolean fullStacktraces) {
    this.fullStacktraces = fullStacktraces;
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    getErrorsCollector(context).ifPresent(collector -> {
      throw new IllegalStateException("Errors collector already exists: " + collector);
    });
    ErrorsCollector errorsCollector = new SoftAssertsErrorsCollector();
    addListener(LISTENER_SOFT_ASSERT, errorsCollector);
    context.getStore(namespace).put(LISTENER_SOFT_ASSERT, errorsCollector);
  }

  @Override
  public void beforeEach(final ExtensionContext context) {
    getErrorsCollector(context).map(collector -> {
      ErrorsCollector perTestCollector = collector.copy();
      context.getStore(namespace).put(LISTENER_SOFT_ASSERT, perTestCollector);
      addListener(LISTENER_SOFT_ASSERT, perTestCollector);
      return collector;
    }).orElseThrow(() -> new IllegalStateException("Errors collector doesn't exist"));
  }

  @Override
  public void afterEach(final ExtensionContext context) {
    getErrorsCollector(context).ifPresent(collector -> {
        collector.cleanAndThrowAssertionError(context.getDisplayName(), context.getExecutionException().orElse(null), fullStacktraces);
      }
    );
  }

  @Override
  public void afterAll(ExtensionContext context) {
    removeListener(LISTENER_SOFT_ASSERT);
    ErrorsCollector errorsCollector = context.getStore(namespace).remove(LISTENER_SOFT_ASSERT, ErrorsCollector.class);
    //noinspection ConstantValue
    if (errorsCollector != null) {
      errorsCollector.cleanAndThrowAssertionError(context.getDisplayName(),
        context.getExecutionException().orElse(null), fullStacktraces
      );
    }
  }

  private Optional<ErrorsCollector> getErrorsCollector(ExtensionContext context) {
    return Optional.ofNullable(
      context.getStore(namespace).get(LISTENER_SOFT_ASSERT, ErrorsCollector.class)
    );
  }
}
