package com.codeborne.selenide;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.Screenshot;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.io.File;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

final class SelenideDriverScreenshotsTest {
  private final SelenideConfig config = new SelenideConfig().timeout(1).pollingInterval(1);
  private final WebDriver webDriver = mock();

  @Test
  void assertionFailuresUseEachDriversLaboratoryEvenWhenTheUnderlyingDriverIsShared() {
    Driver underlyingDriver = new DriverStub(config, webDriver);
    ScreenShotLaboratory firstScreenshots = laboratory("first.png");
    ScreenShotLaboratory secondScreenshots = laboratory("second.png");
    SelenideDriver first = new SelenideDriver(config, underlyingDriver, firstScreenshots);
    SelenideDriver second = new SelenideDriver(config, underlyingDriver, secondScreenshots);
    when(webDriver.findElement(any())).thenThrow(new NoSuchElementException("Missing element"));

    assertThatThrownBy(() -> first.$("#missing").shouldBe(visible))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Screenshot: first.png");
    verify(firstScreenshots).takeScreenshot(first.driver(), true, true);
    verifyNoInteractions(secondScreenshots);

    assertThatThrownBy(() -> second.$$("#missing").shouldHave(size(1)))
      .isInstanceOf(AssertionError.class)
      .hasMessageContaining("Screenshot: second.png");
    verify(secondScreenshots).takeScreenshot(second.driver(), true, true);
  }

  @Test
  void elementScreenshotsUseTheInjectedLaboratory() {
    ScreenShotLaboratory screenshots = mock();
    SelenideDriver browser = new SelenideDriver(config, webDriver, null, screenshots);
    WebElement element = mock();
    File imageFile = new File("element.png");
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    when(webDriver.findElement(any())).thenReturn(element);
    when(screenshots.takeScreenshot(any(Driver.class), same(element))).thenReturn(imageFile);
    when(screenshots.takeScreenshotAsImage(any(Driver.class), same(element))).thenReturn(image);

    assertThat(browser.$("#element").screenshot()).isSameAs(imageFile);
    assertThat(browser.$("#element").screenshotAsImage()).isSameAs(image);
    verify(screenshots).takeScreenshot(browser.driver(), element);
    verify(screenshots).takeScreenshotAsImage(browser.driver(), element);
  }

  @Test
  void explicitDriverScreenshotsUseTheInjectedLaboratory() {
    ScreenShotLaboratory screenshots = mock();
    SelenideDriver browser = new SelenideDriver(config, webDriver, null, screenshots);
    byte[] image = {1, 2, 3};
    when(screenshots.takeScreenshot(any(Driver.class), eq("manual"), eq(true), eq(true)))
      .thenReturn(new Screenshot(null, "manual.png", null));
    when(screenshots.takeScreenShot(any(Driver.class), same(OutputType.BYTES))).thenReturn(image);

    assertThat(browser.screenshot("manual")).isEqualTo("manual.png");
    assertThat(browser.screenshot(OutputType.BYTES)).isSameAs(image);
  }

  @Test
  void injectingScreenshotsPreservesCustomDriverOperations() {
    Driver underlyingDriver = new DriverStub(config, webDriver);
    SelenideDriver browser = new SelenideDriver(config, underlyingDriver, laboratory("custom.png"));

    assertThat(browser.getWebDriver()).isSameAs(webDriver);
    assertThat(browser.getUserAgent()).isEqualTo(underlyingDriver.getUserAgent());
    assertThat(browser.getSessionId()).isEqualTo(underlyingDriver.getSessionId());
    assertThat(browser.driver().actions()).isSameAs(underlyingDriver.actions());

    browser.close();
    verify(webDriver).close();
  }

  private static ScreenShotLaboratory laboratory(String imageUrl) {
    ScreenShotLaboratory screenshots = mock();
    when(screenshots.takeScreenshot(any(Driver.class), anyBoolean(), anyBoolean()))
      .thenReturn(new Screenshot(null, imageUrl, null));
    return screenshots;
  }
}
