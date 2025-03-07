package com.codeborne.selenide.junit5;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.ex.UIAssertionError;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Use this class to automatically take screenshots in case of ANY errors in tests (not only Selenide errors).
 * <p>
 * How to use in Java:
 * <pre>
 *   {@code @ExtendWith({ScreenShooterExtension.class})}
 *    public class MyTest {...}
 * </pre>
 * <p>
 * How to use in Java (with customization):
 * <pre>
 *   public class MyTest {
 *    {@code @RegisterExtension}
 *     static ScreenShooterExtension screenshotEmAll = new ScreenShooterExtension(true);
 *     ...
 *   }
 * </pre>
 * <p>
 * How to use in Kotlin:
 *
 * <pre>
 *    {@code @ExtendWith(ScreenShooterExtension::class)}
 *     public class MyTest {...}
 * </pre>
 * <p>
 * How to use in Kotlin (with customization):
 *
 * <pre>
 *   public class MyTest {
 *     companion object {
 *      {@code @JvmField}
 *      {@code @RegisterExtension}
 *       val screenshotEmAll: ScreenShooterExtension = ScreenShooterExtension(true);
 *     }
 *     ...
 *   }
 * </pre>
 *
 * <p>
 *   Restrictions:
 * </p>
 * <p>
 *   This extension can only take screenshots for "static" webdriver managed by Selenide.
 *   It doesn't take screenshots for webdrivers created by your code, e.g. using {@code new SelenideDriver()}.
 * </p>
 *
 * @author Aliaksandr Rasolka
 */
public class ScreenShooterExtension implements BeforeEachCallback, AfterTestExecutionCallback {
  private static final Logger log = LoggerFactory.getLogger(ScreenShooterExtension.class);

  private final boolean captureSuccessfulTests;

  public ScreenShooterExtension() {
    this(false);
  }

  /**
   * @param captureSuccessfulTests param that indicate if you need to capture successful tests
   */
  public ScreenShooterExtension(final boolean captureSuccessfulTests) {
    this.captureSuccessfulTests = captureSuccessfulTests;
  }

  /**
   * One-liner to configure Configuration.reportsFolder property.
   *
   * @param folderWithScreenshots Folder to put screenshots to
   * @return current extension instance
   */
  @CanIgnoreReturnValue
  public ScreenShooterExtension to(final String folderWithScreenshots) {
    Configuration.reportsFolder = folderWithScreenshots;
    return this;
  }

  @Override
  public void beforeEach(final ExtensionContext context) {
    final Optional<Class<?>> testClass = context.getTestClass();
    final String className = testClass.map(Class::getName).orElse("EmptyClass");

    final Optional<Method> testMethod = context.getTestMethod();
    final String methodName = testMethod.map(Method::getName).orElse("emptyMethod");

    Screenshots.startContext(className, methodName);
  }

  @Override
  public void afterTestExecution(final ExtensionContext context) {
    if (captureSuccessfulTests) {
      log.info(Screenshots.saveScreenshotAndPageSource());
    } else {
      context.getExecutionException().ifPresent(error -> {
        if (!(error instanceof UIAssertionError)) {
          log.info(Screenshots.saveScreenshotAndPageSource());
        }
      });
    }
    Screenshots.finishContext();
  }
}
