package com.codeborne.selenide.appium;

import com.codeborne.selenide.BaseElementsCollection;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.LazyWebElementSnapshot;
import com.codeborne.selenide.impl.NoOpsList;
import com.codeborne.selenide.impl.SelenidePageFactory;
import com.codeborne.selenide.impl.WebElementSource;
import io.appium.java_client.HasBrowserCheck;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.DefaultElementByBuilder;
import io.appium.java_client.pagefactory.bys.builder.AppiumByBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Optional;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;
import static io.appium.java_client.remote.options.SupportsAutomationNameOption.AUTOMATION_NAME_OPTION;

@ParametersAreNonnullByDefault
public class SelenideAppiumPageFactory extends SelenidePageFactory {
  private static final Logger logger = LoggerFactory.getLogger(SelenideAppiumPageFactory.class);

  @Override
  @Nonnull
  @CheckReturnValue
  protected By findSelector(Driver driver, Field field) {
    AppiumByBuilder builder = byBuilder(driver);
    builder.setAnnotated(field);
    By selector = builder.buildBy();
    return selector != null ? selector : super.findSelector(driver, field);
  }

  @Nonnull
  @CheckReturnValue
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

  @Nonnull
  @Override
  @CheckReturnValue
  protected FieldDecorator defaultFieldDecorator(Driver driver, @Nullable WebElementSource searchContext) {
    SearchContext context = getSearchContext(driver, searchContext);
    return new AppiumFieldDecorator(context);
  }

  @CheckReturnValue
  @Nullable
  @Override
  public Object decorate(
    ClassLoader loader,
    Driver driver,
    @Nullable WebElementSource searchContext,
    Field field,
    By selector,
    Type[] genericTypes
  ) {
    if (selector instanceof ByIdOrName) {
      return decorateWithAppium(loader, searchContext, field);
    }
    return super.decorate(loader, driver, searchContext, field, selector, genericTypes);
  }

  @Nullable
  @CheckReturnValue
  private Object decorateWithAppium(ClassLoader loader, @Nullable WebElementSource searchContext, Field field) {
    if (searchContext == null) {
      logger.warn("Cannot initialize field {}", field);
      return null;
    }
    AppiumFieldDecorator defaultAppiumFieldDecorator = new AppiumFieldDecorator(searchContext.getWebElement(), Duration.ZERO);
    Object appiumElement = defaultAppiumFieldDecorator.decorate(loader, field);
    if (appiumElement instanceof WebElement) {
      // TODO Make appiumElement lazy-loaded
      return Selenide.$((WebElement) appiumElement);
    }
    return appiumElement;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  protected SelenideElement decorateWebElement(Driver driver, @Nullable WebElementSource searchContext, By selector,
                                               Field field, @Nullable String alias) {
    return shouldCache(field) ?
      LazyWebElementSnapshot.wrap(new ElementFinder(driver, searchContext, selector, 0, alias)) :
      ElementFinder.wrap(driver, SelenideAppiumElement.class, searchContext, selector, 0, alias);
  }

  @Override
  @Nonnull
  @CheckReturnValue
  protected BaseElementsCollection<? extends SelenideElement, ? extends BaseElementsCollection<?, ?>> createCollection(
    CollectionSource collection, Class<?> klass
  ) {
    return klass.isAssignableFrom(SelenideAppiumList.class) ?
      new SelenideAppiumList(collection) :
      super.createCollection(collection, klass);
  }

  private static class SelenideAppiumList extends SelenideAppiumCollection implements NoOpsList<SelenideAppiumElement> {
    SelenideAppiumList(CollectionSource collection) {
      super(collection);
    }
  }
}
