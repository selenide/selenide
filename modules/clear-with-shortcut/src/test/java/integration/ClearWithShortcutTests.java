package integration;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
  InputFieldTest.class,
  SetValueEventsTest.class,
  TransparencyAndOtherNonStandardVisibilityTest.class,
  ReadonlyElementsTest.class,
  ShadowElementTest.class,
  ShadowDomInsideIFrameTest.class,
  AutoCompleteTest.class,
  FastSetValueTest.class,
  SetDateValueTest.class,
  GetSetValueTest.class
})
public class ClearWithShortcutTests {
}
