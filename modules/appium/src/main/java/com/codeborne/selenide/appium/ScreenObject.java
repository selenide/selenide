package com.codeborne.selenide.appium;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Selenide.page;

public class ScreenObject {
  /**
   * Create a Page Object instance.
   * <br/>
   * It's just an alias for {@link Selenide#page(Class)}
   *
   * @see Selenide#page(Class)
   */
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
  public static <PageObjectClass, T extends PageObjectClass> PageObjectClass screen(T pageObject) {
    return page(pageObject);
  }
}
