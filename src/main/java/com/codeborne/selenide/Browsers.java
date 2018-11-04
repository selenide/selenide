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
   * <p>
   * {@code <dependency org="org.seleniumhq.selenium" name="selenium-htmlunit-driver"
   *                         rev="2.+" conf= "test->default"/>}
   * </p>
   * <p>It is also possible to run HtmlUnitDriver so that it emulates different browsers</p>
   * <p>{@code java -Dbrowser=htmlunit:firefox}</p>
   * <p>{@code java -Dbrowser=htmlunit:chrome}</p>
   * <p>{@code java -Dbrowser=htmlunit:internet explorer   (default)}</p>
   * <p>etc</p>
   */
  public static final String HTMLUNIT = "htmlunit";

  /**
   * To use PhantomJS, you need to set system property: -Dselenide.browser=phantomjs
   */
  public static final String PHANTOMJS = "phantomjs";

  /**
   * To use OperaDriver, you need to include extra dependency to your project:
   * &lt;dependency org="com.opera" name="operadriver" rev="1.5" conf="test-&gt;default"/&gt;
   */
  public static final String OPERA = "opera";

  /**
   * To use JbrowserDriver, you need to include extra dependency to your project:
   * {@code <dependency org="com.machinepublishers" name="jbrowserdriver" rev="[0.13.0, 2.0)" conf="test-&gt;default"/&gt;}
   */
  public static final String JBROWSER = "jbrowser";
}
