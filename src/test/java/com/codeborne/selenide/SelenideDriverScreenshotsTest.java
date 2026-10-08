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
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Collections.emptyList;
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
  void assertionFailuresUseEachBrowsersLaboratory() {
    ScreenShotLaboratory firstScreenshots = laboratory("first.png");
    ScreenShotLaboratory secondScreenshots = laboratory("second.png");
    SelenideDriver first = new SelenideDriver(config, webDriver, null, firstScreenshots);
    SelenideDriver second = new SelenideDriver(config, webDriver, null, secondScreenshots);
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
  void lazyDriverUsesTheInjectedLaboratory() {
    ScreenShotLaboratory screenshots = mock();
    SelenideDriver browser = new SelenideDriver(config, emptyList(), screenshots);

    assertThat(browser.driver().screenshots()).isSameAs(screenshots);
  }

  @Test
  void driversWithoutInjectedLaboratoryUseTheDefaultLaboratory() {
    ScreenShotLaboratory defaultScreenshots = inject(ScreenShotLaboratory.class);

    assertThat(new SelenideDriver(config, webDriver, null).driver().screenshots()).isSameAs(defaultScreenshots);
    assertThat(new SelenideDriver(config, emptyList()).driver().screenshots()).isSameAs(defaultScreenshots);
    assertThat(new DriverStub(config, webDriver).screenshots()).isSameAs(defaultScreenshots);
  }

  @Test
  void customDriversCanProvideTheirOwnLaboratory() {
    ScreenShotLaboratory screenshots = laboratory("custom.png");
    Driver customDriver = new DriverStub(config, webDriver) {
      @Override
      public ScreenShotLaboratory screenshots() {
        return screenshots;
      }
    };
    SelenideDriver browser = new SelenideDriver(config, customDriver);
    when(webDriver.findElement(any())).thenThrow(new NoSuchElementException("Missing element"));

    assertThatThrownBy(() -> browser.$("#missing").shouldBe(visible))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Screenshot: custom.png");
    verify(screenshots).takeScreenshot(customDriver, true, true);
  }

  @Test
  @SuppressWarnings("deprecation")
  void deprecatedConstructor_usesTheLaboratoryOnlyForExplicitScreenshots() {
    ScreenShotLaboratory screenshots = mock();
    ScreenShotLaboratory driverScreenshots = laboratory("driver.png");
    Driver customDriver = new DriverStub(config, webDriver) {
      @Override
      public ScreenShotLaboratory screenshots() {
        return driverScreenshots;
      }
    };
    SelenideDriver browser = new SelenideDriver(config, customDriver, screenshots);
    when(screenshots.takeScreenshot(any(Driver.class), eq("manual"), eq(true), eq(true)))
      .thenReturn(new Screenshot(null, "manual.png", null));
    when(webDriver.findElement(any())).thenThrow(new NoSuchElementException("Missing element"));

    assertThat(browser.screenshot("manual")).isEqualTo("manual.png");
    assertThatThrownBy(() -> browser.$("#missing").shouldBe(visible))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Screenshot: driver.png");
  }

  private static ScreenShotLaboratory laboratory(String imageUrl) {
    ScreenShotLaboratory screenshots = mock();
    when(screenshots.takeScreenshot(any(Driver.class), anyBoolean(), anyBoolean()))
      .thenReturn(new Screenshot(null, imageUrl, null));
    return screenshots;
  }
}
