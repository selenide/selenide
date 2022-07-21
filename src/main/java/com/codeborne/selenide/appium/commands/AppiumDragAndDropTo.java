package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.DragAndDropTo;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.Optional;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.WebdriverUnwrapper.cast;

@ParametersAreNonnullByDefault
public class AppiumDragAndDropTo extends DragAndDropTo {

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    Optional<PerformsTouchActions> appiumDriver = cast(locator.driver(), PerformsTouchActions.class);
    if (!appiumDriver.isPresent()) {
      return super.execute(proxy, locator, args);
    }

    SelenideElement target = findTarget(locator.driver(), args);
    target.shouldBe(visible);

    new TouchAction<>(appiumDriver.get())
      .longPress(ElementOption.element(locator.getWebElement()))
      .waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))
      .moveTo(ElementOption.element(target.getWrappedElement()))
      .release()
      .perform();

    return proxy;
  }
}
