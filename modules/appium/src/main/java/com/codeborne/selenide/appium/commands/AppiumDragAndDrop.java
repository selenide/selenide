package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.DragAndDrop;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.ACTIONS;
import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.JS;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

public class AppiumDragAndDrop extends DragAndDrop {
  private static final Logger log = LoggerFactory.getLogger(AppiumDragAndDrop.class);

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    if (!isMobile(locator.driver())) {
      super.execute(locator, args);
      return;
    }

    DragAndDropOptions options = dragAndDropOptions(args, ACTIONS);
    if (options.method() == JS) {
      log.debug("Drag'n'Drop with JavaScript is not supported in mobile apps");
    }
    SelenideElement target = options.getTarget(locator.driver());
    target.shouldBe(visible);

    Sequence sequence = getSequenceToPerformDragAndDrop(getCenter(locator.getWebElement()), getCenter(target));
    ((Interactive) locator.driver().getWebDriver()).perform(singletonList(sequence));
  }

  private Sequence getSequenceToPerformDragAndDrop(Point source, Point target) {
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    return new Sequence(finger, 1)
      .addAction(finger.createPointerMove(ofMillis(0), PointerInput.Origin.viewport(), source.x, source.y))
      .addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()))
      .addAction(new Pause(finger, ofMillis(600)))
      .addAction(finger.createPointerMove(ofMillis(600),
        PointerInput.Origin.viewport(),
        target.x, target.y))
      .addAction(finger.createPointerUp(PointerInput.MouseButton.MIDDLE.asArg()));
  }

  private Point getCenter(WebElement element) {
    int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
    int y = element.getLocation().getY() + element.getSize().getHeight() / 2;
    return new Point(x, y);
  }
}
