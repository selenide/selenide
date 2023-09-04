package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.appium.AppiumClickOptions;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import com.codeborne.selenide.commands.Commands;
import io.appium.java_client.AppiumDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.Map;

import static com.codeborne.selenide.appium.SelenideAppium.$;
import static org.assertj.core.api.Assertions.assertThat;

public class AppiumDoubleTapTest {

  private static AppiumDoubleTap appiumDoubleTapSpy;
  private static AppiumDoubleTap appiumDoubleTap;
  private static Map<String, Command<SelenideElement>> commandsMap;

  @BeforeAll
  @SuppressWarnings("unchecked")
  public static void injectSpy() throws NoSuchFieldException, IllegalAccessException {
    Commands commandsInstance = Commands.getInstance();
    Field f = Commands.class.getDeclaredField("commands");
    f.setAccessible(true);
    commandsMap = (Map<String, Command<SelenideElement>>) f.get(commandsInstance);
    appiumDoubleTap = (AppiumDoubleTap) commandsMap.get("doubleTap");
    appiumDoubleTapSpy = Mockito.spy(appiumDoubleTap);
    commandsMap.put("doubleTap", appiumDoubleTapSpy);
  }

  @AfterEach
  public void resetSpy() {
    Mockito.reset(appiumDoubleTapSpy);
    Mockito.clearInvocations(appiumDoubleTapSpy);
  }

  @AfterAll
  public static void removeSpy() {
    commandsMap.put("doubleTap", appiumDoubleTap);
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void doubleTap() {
    By selector = By.id("fakeSelector");
    SelenideAppiumElement element = $(selector);
    AppiumDriver mockedAppiumDriver = getMockedAppiumDriver(selector);

    try (MockedStatic<WebDriverRunner> webDriverRunnerMock = Mockito.mockStatic(WebDriverRunner.class)) {
      webDriverRunnerMock.when(WebDriverRunner::getWebDriver).thenReturn(mockedAppiumDriver);
      element.doubleTap();
    }

    ArgumentCaptor<AppiumClickOptions> captor = ArgumentCaptor.forClass(AppiumClickOptions.class);
    Mockito.verify(appiumDoubleTapSpy, Mockito.times(1)).click(Mockito.any(), Mockito.any(), captor.capture());
    assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(AppiumClickOptions.doubleTap());
  }

  private static AppiumDriver getMockedAppiumDriver(By selector) {
    WebElement mockedWebElement = Mockito.mock(WebElement.class);
    Mockito.when(mockedWebElement.getLocation()).thenReturn(new Point(100, 100));
    Mockito.when(mockedWebElement.getSize()).thenReturn(new Dimension(30, 30));
    AppiumDriver mockedAppiumDriver = Mockito.mock(AppiumDriver.class);
    Mockito.when(mockedAppiumDriver.findElement(selector)).thenReturn(mockedWebElement);
    return mockedAppiumDriver;
  }
}
