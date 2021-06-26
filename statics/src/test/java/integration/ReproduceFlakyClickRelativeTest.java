package integration;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SuiteDisplayName("JUnit Platform Suite Demo")
@SelectClasses({
  ChecksOnMissingParentElementTest.class,
  SessionStorageTest.class,
  SoftAssertJUnit5Test.class,
  FluentWaitUsage.class,
  ClickRelativeTest.class
})
public class ReproduceFlakyClickRelativeTest {
}
