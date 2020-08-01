package com.codeborne.selenide;

/**
 * Constants for all browsers supported by Selenide out of the box
 */
public interface Browsers {
  String CHROME = "chrome";
  String IE = "ie";
  String INTERNET_EXPLORER = "internet explorer";
  String EDGE = "edge";
  String FIREFOX = "firefox";

  /**
   * It is only supported for some ESR versions of Firefox up to ESR 52
   */
  String LEGACY_FIREFOX = "legacy_firefox";

  /**
   * To use OperaDriver, you need to include extra dependency to your project:
   * &lt;dependency org="com.opera" name="operadriver" rev="1.5" conf="test-&gt;default"/&gt;
   */
  String OPERA = "opera";

  String SAFARI = "safari";
}
