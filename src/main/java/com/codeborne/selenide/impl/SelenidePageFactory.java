package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Factory class to make using Page Objects simpler and easier.
 *
 * @see <a href="https://github.com/SeleniumHQ/selenium/wiki/PageObjects">Page Objects Wiki</a>
 */
@ParametersAreNonnullByDefault
public class SelenidePageFactory {
  @CheckReturnValue
  @Nonnull
  public <PageObjectClass> PageObjectClass page(Driver driver, Class<PageObjectClass> pageObjectClass) {
    try {
      Constructor<PageObjectClass> constructor = pageObjectClass.getDeclaredConstructor();
      constructor.setAccessible(true);
      return page(driver, constructor.newInstance());
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to create new instance of " + pageObjectClass, e);
    }
  }

  @CheckReturnValue
  @Nonnull
  public <PageObjectClass, T extends PageObjectClass> PageObjectClass page(Driver driver, T pageObject) {
    Type[] types = pageObject.getClass().getGenericInterfaces();
    initElements(new SelenideFieldDecorator(this, driver, driver.getWebDriver()), pageObject, types);
    return pageObject;
  }

  /**
   * Similar to the other "initElements" methods, but takes an {@link FieldDecorator} which is used
   * for decorating each of the fields.
   *
   * @param decorator the decorator to use
   * @param page      The object to decorate the fields of
   */
  public void initElements(SelenideFieldDecorator decorator, Object page, Type[] genericTypes) {
    Class<?> proxyIn = page.getClass();
    while (proxyIn != Object.class) {
      proxyFields(decorator, page, proxyIn, genericTypes);
      proxyIn = proxyIn.getSuperclass();
    }
  }

  private void proxyFields(SelenideFieldDecorator decorator, Object page, Class<?> proxyIn, Type[] genericTypes) {
    Field[] fields = proxyIn.getDeclaredFields();
    for (Field field : fields) {
      if (isInitialized(page, field)) {
        continue;
      }
      Object value = decorator.decorate(page.getClass().getClassLoader(), field, genericTypes);
      if (value != null) {
        try {
          field.setAccessible(true);
          field.set(page, value);
        }
        catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private boolean isInitialized(Object page, Field field) {
    try {
      field.setAccessible(true);
      return field.get(page) != null;
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
