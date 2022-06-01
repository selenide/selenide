package com.codeborne.selenide;

import com.codeborne.selenide.impl.CollectionSource;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.impl.Alias.NONE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ParametersAreNonnullByDefault
public class Mocks {
  @Nonnull
  @CheckReturnValue
  public static SelenideElement mockElement(String text) {
    return mockElement("div", text);
  }

  @Nonnull
  @CheckReturnValue
  public static SelenideElement mockElement(String tag, String text) {
    SelenideElement element = mock(SelenideElement.class);
    when(element.getTagName()).thenReturn(tag);
    when(element.getText()).thenReturn(text);
    return element;
  }

  @Nonnull
  @CheckReturnValue
  public static WebElement elementWithAttribute(String name, String value) {
    WebElement element = mock(WebElement.class);
    when(element.getAttribute(name)).thenReturn(value);
    return element;
  }

  @Nonnull
  @CheckReturnValue
  public static SelenideElement mockSelect(WebElement... options) {
    SelenideElement select = mockElement("select", "");
    when(select.isSelected()).thenReturn(true);
    when(select.findElements(By.tagName("option"))).thenReturn(asList(options));
    return select;
  }

  @Nonnull
  @CheckReturnValue
  public static WebElement option(String text) {
    return option(text, false);
  }

  @Nonnull
  @CheckReturnValue
  public static WebElement option(String text, boolean selected) {
    WebElement option = mockElement("option", text);
    when(option.isSelected()).thenReturn(selected);
    return option;
  }

  @Nonnull
  @CheckReturnValue
  public static WebElement mockWebElement(String tag, String text) {
    WebElement element = mock(WebElement.class);
    when(element.getTagName()).thenReturn(tag);
    when(element.getText()).thenReturn(text);
    when(element.isDisplayed()).thenReturn(true);
    return element;
  }

  @Nonnull
  @CheckReturnValue
  public static CollectionSource mockCollection(String description, WebElement... elements) {
    Driver driver = mock(Driver.class);
    when(driver.config()).thenReturn(new SelenideConfig());

    CollectionSource collection = mock(CollectionSource.class);
    when(collection.driver()).thenReturn(driver);
    when(collection.getSearchCriteria()).thenReturn(description);
    when(collection.shortDescription()).thenReturn(description);
    when(collection.description()).thenReturn(description);
    when(collection.getAlias()).thenReturn(NONE);
    when(collection.getElements()).thenReturn(asList(elements));
    for (int i = 0; i < elements.length; i++) {
      when(collection.getElement(i)).thenReturn(elements[i]);
    }
    return collection;
  }

  public static void givenScreenSize(RemoteWebDriver webDriver, long fullWidth, long fullHeight, long viewWidth, long viewHeight) {
    when(webDriver.executeScript(anyString())).thenReturn(fullWidth, fullHeight, viewWidth, viewHeight);
  }

  public static void givenCdpScreenshot(HasCdp webDriver, String png) {
    String base64Png = Base64.encodeBase64String(png.getBytes(UTF_8));
    when(webDriver.executeCdpCommand(eq("Page.captureScreenshot"), any())).thenReturn(ImmutableMap.of("data", base64Png));
  }
}
