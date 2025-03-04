package integration.customcommands;

import com.codeborne.selenide.SelenideElement;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

public interface MySelenideElement extends SelenideElement {
  @CanIgnoreReturnValue
  MySelenideElement tripleClick();

  @CanIgnoreReturnValue
  MySelenideElement quadrupleClick();
}
