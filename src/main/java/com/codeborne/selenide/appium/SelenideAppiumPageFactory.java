package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.impl.SelenidePageFactory;
import io.appium.java_client.HasSessionDetails;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.DefaultElementByBuilder;
import io.appium.java_client.pagefactory.bys.builder.AppiumByBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.ByIdOrName;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

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
    if (!HasSessionDetails.class.isAssignableFrom(driver.getWebDriver().getClass())) {
      return new DefaultElementByBuilder(null, null);
    }
    else {
      HasSessionDetails d = (HasSessionDetails) driver.getWebDriver();
      return new DefaultElementByBuilder(d.getPlatformName(), d.getAutomationName());
    }
  }

  @CheckReturnValue
  @Nullable
  @Override
  public Object decorate(ClassLoader loader,
                         Driver driver, SearchContext searchContext,
                         Field field, By selector, Type[] genericTypes) {
    if (selector instanceof ByIdOrName) {
      return decorateWithAppium(loader, searchContext, field);
    }

    return super.decorate(loader, driver, searchContext, field, selector, genericTypes);
  }

  private Object decorateWithAppium(ClassLoader loader, SearchContext searchContext, Field field) {
    AppiumFieldDecorator defaultAppiumFieldDecorator = new AppiumFieldDecorator(searchContext);
    Object appiumElement = defaultAppiumFieldDecorator.decorate(loader, field);
    if (appiumElement instanceof MobileElement) {
      // TODO Make appiumElement lazy-loaded
      return Selenide.$((MobileElement) appiumElement);
    }
    return appiumElement;
  }
}
