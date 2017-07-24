package com.codeborne.selenide.appium;

import com.codeborne.selenide.SelenidePageFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class ScreenObject {
  /**
   * Create a Page Object instance.
   *
   * @see PageFactory#initElements(WebDriver, Class)
   */
  public static <PageObjectClass, T extends PageObjectClass> PageObjectClass screen(T pageObject) {
    SelenidePageFactory.initElements(new SelenideAppiumFieldDecorator(getWebDriver()), pageObject);
    return pageObject;
  }
}
