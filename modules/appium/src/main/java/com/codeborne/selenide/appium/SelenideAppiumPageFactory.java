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
import io.appium.java_client.pagefactory.AndroidFindAll;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AndroidFindByAllSet;
import io.appium.java_client.pagefactory.AndroidFindByChainSet;
import io.appium.java_client.pagefactory.AndroidFindBySet;
import io.appium.java_client.pagefactory.AndroidFindBys;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.DefaultElementByBuilder;
import io.appium.java_client.pagefactory.bys.builder.AppiumByBuilder;
import io.appium.java_client.pagefactory.iOSXCUITFindAll;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindByAllSet;
import io.appium.java_client.pagefactory.iOSXCUITFindByChainSet;
import io.appium.java_client.pagefactory.iOSXCUITFindBySet;
import io.appium.java_client.pagefactory.iOSXCUITFindBys;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;
import static io.appium.java_client.remote.options.SupportsAutomationNameOption.AUTOMATION_NAME_OPTION;

public class SelenideAppiumPageFactory extends SelenidePageFactory {
  private static final Logger logger = LoggerFactory.getLogger(SelenideAppiumPageFactory.class);
  private final List<Class<? extends Annotation>> platformAnnotations =
    List.of(AndroidFindBy.class, AndroidFindBys.class, AndroidFindAll.class, AndroidFindByAllSet.class, AndroidFindByChainSet.class,
      AndroidFindBySet.class, iOSXCUITFindBy.class, iOSXCUITFindBys.class, iOSXCUITFindAll.class, iOSXCUITFindByAllSet.class,
      iOSXCUITFindByChainSet.class, iOSXCUITFindBySet.class);

  @Override
  protected By findSelector(Driver driver, Field field) {
    AppiumByBuilder builder = byBuilder(driver, field);
    builder.setAnnotated(field);
    By selector = builder.buildBy();
    return selector != null ? selector : super.findSelector(driver, field);
  }

  private DefaultElementByBuilder byBuilder(Driver driver, Field field) {
    if (!isPlatformAnnotationAdded(field)) {
      return new DefaultElementByBuilder(null, null);
    }

    if (!driver.hasWebDriverStarted()) {
      throw new WebDriverException("The SelenideAppiumPageFactory requires a webdriver instance to be created before page" +
        " initialization; No webdriver is bound to current thread. You need to call open() first");
    }
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

  @Override
  protected FieldDecorator defaultFieldDecorator(Driver driver, @Nullable WebElementSource searchContext) {
    SearchContext context = getSearchContext(driver, searchContext);
    return new AppiumFieldDecorator(context);
  }

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

  @Override
  protected SelenideElement createSelf(WebElementSource searchContext) {
    return ElementFinder.wrap(SelenideAppiumElement.class, searchContext);
  }

  @Override
  protected SelenideElement decorateWebElement(Driver driver, @Nullable WebElementSource searchContext, By selector,
                                               Field field, @Nullable String alias) {
    return shouldCache(field) ?
      LazyWebElementSnapshot.wrap(new ElementFinder(driver, searchContext, selector, 0, alias)) :
      ElementFinder.wrap(driver, getTargetType(field), searchContext, selector, 0, alias);
  }

  @Override
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

  private boolean isPlatformAnnotationAdded(Field field) {
    return platformAnnotations.stream().anyMatch(field::isAnnotationPresent);
  }

  @SuppressWarnings("unchecked")
  private <T extends SelenideAppiumElement> Class<T> getTargetType(Field field) {
    return SelenideAppiumElement.class.isAssignableFrom(field.getType()) ?
      (Class<T>) field.getType() : (Class<T>) SelenideAppiumElement.class;
  }
}
