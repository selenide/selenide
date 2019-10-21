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
  public static final String LEGACY_FIREFOX = "legacy_firefox";

  /**
   * To use Safari webdriver, you need to include extra dependency to your project:
   * &lt;dependency org="org.seleniumhq.selenium" name="selenium-safari-driver" rev="2.+" conf="test-&gt;default"/&gt;
   */
  public static final String SAFARI = "safari";

  /**
   * To use the HtmlUnitDriver, you need to add the following dependency to your project:
   * &lt;dependency org="org.seleniumhq" name="htmlunit-driver" rev="2.36.0" conf="test-&gt;default"/&gt;
   */
  public static final String HTMLUNIT = "htmlunit";

  /**
   * To use PhantomJS, you need to include extra dependency to your project:
   * &lt;dependency org="com.codeborne" name="phantomjsdriver" rev="1.4.4" conf="test-&gt;default"/&gt;
   */
  public static final String PHANTOMJS = "phantomjs";

  /**
   * To use OperaDriver, you need to include extra dependency to your project:
   * &lt;dependency org="com.opera" name="operadriver" rev="1.5" conf="test-&gt;default"/&gt;
   */
  public static final String OPERA = "opera";

  /**
   * To use JbrowserDriver, you need to include extra dependency to your project:
   * &lt;dependency org="com.machinepublishers" name="jbrowserdriver" rev="[0.13.0, 2.0)" conf="test-&gt;default"/&gt;
   */
  public static final String JBROWSER = "jbrowser";
}
