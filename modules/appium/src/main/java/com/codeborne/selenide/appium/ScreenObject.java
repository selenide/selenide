package com.codeborne.selenide.appium;

import com.codeborne.selenide.Selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.page;

@ParametersAreNonnullByDefault
public class ScreenObject {
  /**
   * Create a Page Object instance.
   * <br/>
   * It's just an alias for {@link Selenide#page(Class)}
   *
   * @see Selenide#page(Class)
   */
  @Nonnull
  @CheckReturnValue
  public static <PageObjectClass> PageObjectClass screen(Class<PageObjectClass> pageObjectClass) {
    return page(pageObjectClass);
  }

  /**
   * Initialize a Page Object fields annotated with @FindBy, @AndroidFindBy, @iOSFindBy etc.
   * <br/>
   * It's just an alias for {@link Selenide#page(Object)}
   *
   * @see Selenide#page(Object)
   */
  @Nonnull
  @CheckReturnValue
  public static <PageObjectClass, T extends PageObjectClass> PageObjectClass screen(T pageObject) {
    return page(pageObject);
  }
}
