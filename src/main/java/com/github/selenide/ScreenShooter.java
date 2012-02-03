package com.github.selenide;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.concurrent.atomic.AtomicInteger;

import static com.github.selenide.WebDriverRunner.takeScreenShot;

public class ScreenShooter extends BlockJUnit4ClassRunner {
  public static int FAILURES_LIMIT = 3;
  private final AtomicInteger errors = new AtomicInteger();

  public ScreenShooter(Class<?> clazz) throws InitializationError {
    super(clazz);
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, Object test) {
    if (errors.get() > FAILURES_LIMIT) {
      throw new IllegalStateException(errors.get() + " tests already failed; don't waste time for others.");
    }

    return super.methodInvoker(method, test);
  }

  @Override
   protected void runChild(FrameworkMethod method, RunNotifier notifier) {
     UITestListener uiTestListener = new UITestListener();
     notifier.addListener(uiTestListener);

     try {
       super.runChild(method, notifier);
     }
     finally {
       notifier.removeListener(uiTestListener);
     }
   }


  private class UITestListener extends RunListener {
    @Override
    public void testFailure(Failure failure) throws Exception {
      errors.incrementAndGet();
      takeScreenShot(failure.getTestHeader());
      super.testFailure(failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
      errors.incrementAndGet();
      takeScreenShot(failure.getTestHeader());
      super.testAssumptionFailure(failure);
    }
  }
}
