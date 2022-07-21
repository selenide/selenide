package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.impl.SelenidePageFactory;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.HasBrowserCheck;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.DefaultElementByBuilder;
import io.appium.java_client.pagefactory.bys.builder.AppiumByBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ByIdOrName;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.cast;
import static io.appium.java_client.remote.options.SupportsAutomationNameOption.AUTOMATION_NAME_OPTION;

@ParametersAreNonnullByDefault
public class SelenideAppiumPageFactory extends SelenidePageFactory {
  @Override
  @Nonnull
  protected By findSelector(Driver driver, Field field) {
    AppiumByBuilder builder = byBuilder(driver);
    builder.setAnnotated(field);
    By selector = builder.buildBy();
    return selector != null ? selector : super.findSelector(driver, field);
  }

  private DefaultElementByBuilder byBuilder(Driver driver) {
    Optional<HasBrowserCheck> hasBrowserCheck = cast(driver, HasBrowserCheck.class);
    if (hasBrowserCheck.isPresent() && hasBrowserCheck.get().isBrowser()) {
      return new DefaultElementByBuilder(null, null);
    }

    Optional<HasCapabilities> hasCapabilities = cast(driver, HasCapabilities.class);
    if (hasCapabilities.isPresent()) {
      Capabilities d = hasCapabilities.get().getCapabilities();
      String platform = String.valueOf(d.getPlatformName());
      String automationName = String.valueOf(d.getCapability(AUTOMATION_NAME_OPTION));
      return new DefaultElementByBuilder(platform, automationName);
    }

    return new DefaultElementByBuilder(null, null);
  }

  @CheckReturnValue
  @Nullable
  @Override
  public Object decorate(ClassLoader loader, Driver driver, @Nullable WebElementSource searchContext, Field field, By selector, Type[] genericTypes) {
    if (selector instanceof ByIdOrName) {
      return decorateWithAppium(loader, searchContext, field);
    }
    return super.decorate(loader, driver, searchContext, field, selector, genericTypes);
  }

  private Object decorateWithAppium(ClassLoader loader, @Nullable WebElementSource searchContext, Field field) {
    AppiumFieldDecorator defaultAppiumFieldDecorator = new AppiumFieldDecorator(searchContext.getWebElement());
    Object appiumElement = defaultAppiumFieldDecorator.decorate(loader, field);
    if (appiumElement instanceof WebElement) {
      // TODO Make appiumElement lazy-loaded
      return Selenide.$((WebElement) appiumElement);
    }
    return appiumElement;
  }
}
