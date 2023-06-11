package com.codeborne.selenide.appium;

import static com.codeborne.selenide.Selenide.page;

public class ScreenObject {
  /**
   * Create a Page Object instance.
   */
  public static <PageObjectClass> PageObjectClass screen(Class<PageObjectClass> pageObjectClass) {
    return page(pageObjectClass);
  }

  /**
   * Initialize a Page Object fields annotated with @FindBy, @AndroidFindBy, @iOSFindBy etc.
   */
  public static <PageObjectClass, T extends PageObjectClass> PageObjectClass screen(T pageObject) {
    return page(pageObject);
  }
}
