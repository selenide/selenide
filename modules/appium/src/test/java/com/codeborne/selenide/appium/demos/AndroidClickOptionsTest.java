package com.codeborne.selenide.appium.demos;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

class AndroidClickOptionsTest extends AbstractApiDemosTest{

  void testAndroidTap(){
    $(By.xpath(".//*[@text='Views']")).click();
    //No option to pass AppiumClickOptions
    //Even if I use AppiumClickOptions extends ClickOptions -> constructor is private.
  }
}
