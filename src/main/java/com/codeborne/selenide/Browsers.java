package com.codeborne.selenide;

/**
 * Constants for all browsers supported by Selenide out of the box
 */
public interface Browsers {
  public static final String CHROME = "chrome";
  public static final String IE = "ie";
  public static final String INTERNET_EXPLORER = "internet explorer";
  public static final String EDGE = "edge";
  public static final String FIREFOX = "firefox";
  /**
   * It is only supported for some ESR versions of Firefox up to ESR 52
   */
  public static final String LEGACY_FIREFOX = "legacy_firefox";

  /**
   * To use OperaDriver, you need to include extra dependency to your project:
   * &lt;dependency org="com.opera" name="operadriver" rev="1.5" conf="test-&gt;default"/&gt;
   */
  public static final String OPERA = "opera";
}
