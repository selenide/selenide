package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.FileContent;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.ACTIONS;
import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.JS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DragAndDropToTest {

  private final JSWebDriver webDriver = mock();
  private final DriverStub driver = new DriverStub(webDriver);
  private final WebElementSource locator = mock();
  private final WebElement locatorWebElement = mock();
  private final SelenideElement targetSelenideElement = mock();
  private final WebElement targetWebElement = mock();
  private final DragAndDropTo command = new DragAndDropTo();
  private final FileContent jsSource = new FileContent("drag_and_drop_script.js");

  @BeforeEach
  void setup() {
    when(locator.driver()).thenReturn(driver);
    when(locator.getWebElement()).thenReturn(locatorWebElement);
    when(targetSelenideElement.getWrappedElement()).thenReturn(targetWebElement);
    doNothing().when(locator).checkCondition(anyString(), any(Condition.class), anyBoolean());
  }

  @Test
  public void execute_usingJS() {
    StubTarget target = new StubTarget(targetSelenideElement);

    command.execute(mock(), locator, new Object[]{new DragAndDropOptions(target, JS)});

    verify(targetSelenideElement).shouldBe(visible);

    verify(locator).getWebElement();
    verify(targetSelenideElement).getWrappedElement();

    verify(webDriver).executeScript(eq("return " + jsSource.content()), same(locatorWebElement), same(targetWebElement));
  }

  @Test
  public void execute_usingSeleniumActions() {
    StubTarget target = new StubTarget(targetSelenideElement);
    var actions = driver.actions();
    when(actions.dragAndDrop(any(), any())).thenReturn(actions);

    command.execute(mock(), locator, new Object[]{new DragAndDropOptions(target, ACTIONS)});

    verify(targetSelenideElement).shouldBe(visible);

    verify(locator).getWebElement();
    verify(targetSelenideElement).getWrappedElement();

    verify(actions).dragAndDrop(same(locatorWebElement), same(targetWebElement));
    verify(actions).perform();
  }

  private static class StubTarget implements DragAndDropOptions.DragAndDropTarget {

    private final SelenideElement mock;

    StubTarget(SelenideElement element) {
      mock = element;
    }

    @Override
    public SelenideElement toSelenideElement(@Nonnull Driver driver) {
      return mock;
    }
  }

  interface JSWebDriver extends WebDriver, JavascriptExecutor {
  }
}
