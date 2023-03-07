package com.codeborne.selenide.appium;

import com.codeborne.selenide.SelenideElement;

public interface SelenideAppiumElement extends SelenideElement {
  SelenideAppiumElement hideKeyboard();
  SelenideAppiumElement scrollTo();
  SelenideAppiumElement scroll(AppiumScrollOptions appiumScrollOptions);
}
