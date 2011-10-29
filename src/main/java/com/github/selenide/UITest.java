package com.github.selenide;

import com.github.selenide.jetty.Launcher;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static com.github.selenide.Condition.hasOptions;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(UITestsRunner.class)
public abstract class UITest {
  protected static String browser = System.getProperty("browser", "firefox");
  protected static WebDriver webdriver;
  private static Launcher server;
  public static final long defaultWaitingTimeout = Long.getLong("timeout", 4000);

  @Before
  public void startServer() throws Exception {
    if (server == null) {
      server = createLauncher();
      server.run();
    }
  }

  protected Launcher createLauncher() {
    return new Launcher();
  }

  @BeforeClass
  public static void openBrowser() {
    if (webdriver == null) {
      webdriver = createDriver(browser);
      webdriver.manage().timeouts().implicitlyWait(5, SECONDS);
    }
  }

  @After
  public void hopeToFixIE() {
    if (webdriver != null) {
      clearBrowserCache();
      if (ie()) {
        sleep(1000);
      }
    }
  }

  @AfterClass
  public static void closeBrowser() {
    if (webdriver != null) {
      webdriver.close();
      webdriver = null;
    }
  }

  private static WebDriver createDriver(String browser) {
    if ("chrome".equalsIgnoreCase(browser)) {
      return new ChromeDriver();
    } else if ("ie".equalsIgnoreCase(browser)) {
      DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
      ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
      return new InternetExplorerDriver(ieCapabilities);
    } else {
      return new FirefoxDriver();
    }
  }

  protected static boolean ie() {
    return browser != null && webdriver instanceof InternetExplorerDriver;
  }

  protected static void clearBrowserCache() {
    webdriver.manage().deleteAllCookies();
  }

  protected static void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      fail(e.toString());
    }
  }

  protected static String takeScreenShot(String testName) {
    if (webdriver == null) {
      return null;
    }
    else if (webdriver instanceof TakesScreenshot) {
      try {
        File scrFile = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.FILE);
        String pageSource = webdriver.getPageSource();

        String screenshotFileName = "build/reports/tests/" + testName + ".png";
        String htmlFileName = "build/reports/tests/" + testName + ".html";
        copyFile(scrFile, new File(screenshotFileName));
        IOUtils.write(pageSource, new FileWriter(htmlFileName));
        return screenshotFileName;
      } catch (Exception e) {
        System.err.println(e);
      }
    } else {
      System.err.println("Cannot take screenshot, driver does not support it: " + webdriver);
    }

    return null;
  }

  protected void open(String servletName) {
    if (server.getDefaultWebapp() == null) {
      throw new IllegalStateException("No webapps deployed. Override method createLauncher() to create jetty launcher with your own web application.");
    }
    openRelativeUrl(server.getDefaultWebapp() + servletName);
  }

  protected void openRelativeUrl(String relativeUrl) {
    navigateToAbsoluteUrl("http://localhost:" + server.getPort() + relativeUrl);
  }

  protected void navigateToAbsoluteUrl(String url) {
    webdriver.navigate().to(makeUniqueUrl(url));
    assertTrue(webdriver.findElement(By.tagName("body")).isDisplayed());

    if (ie()) {
      toBeSureThatPageIsNotCached();
    }
  }

  private void toBeSureThatPageIsNotCached() {
    String currentUrl = webdriver.getCurrentUrl();
    if (!currentUrl.contains("timestamp=")) {
      navigateToAbsoluteUrl(currentUrl);
    }
  }

  String makeUniqueUrl(String url) {
    String unique = generateUID();
    final String fullUrl;
    if (url.contains("timestamp=")) {
      fullUrl = url.replaceFirst("(.*)(timestamp=)(.*)([&#].*)", "$1$2" + unique + "$4")
                   .replaceFirst("(.*)(timestamp=)(.*)$", "$1$2" + unique);
    }
    else {
      fullUrl = url.contains("?") ?
          url + "&timestamp=" + unique:
          url + "?timestamp=" + unique;
    }
    return fullUrl;
  }

  String generateUID() {
    return "" + System.nanoTime();
  }

  protected static <T> T fail(String message) {
    Assert.fail(message +
        ", browser.currentUrl=" + webdriver.getCurrentUrl() +
        ", browser.title=" + webdriver.getTitle()
    );
    return null;
  }

  protected static WebElement getElement(By by) {
    try {
      return webdriver.findElement(by);
    } catch (WebDriverException e) {
      return fail("Cannot get element " + by + ", caused by: " + e);
    }
  }

  protected static void setValue(By by, String value) {
    try {
      WebElement element = webdriver.findElement(by);
      setValue(element, value);
      triggerChangeEvent(by);
    } catch (WebDriverException e) {
      fail("Cannot get element " + by + ", caused by: " + e);
    }
  }

  protected static void setValue(WebElement element, String value) {
    element.clear();
    element.sendKeys(value);
  }

  protected static void click(By by) {
    // This doesn't work stably in Windows:
    // getElement(by).click();

    // so we had to create a workaround using JavaScript:
    executeJavaScript(getJQuerySelector(by) + ".click();");
  }

  protected static void click(By by, int index) {
    executeJavaScript(getJQuerySelector(by) + ".eq(" + index + ").click();");
  }

  protected static void triggerChangeEvent(By by) {
    executeJavaScript(getJQuerySelector(by) + ".change();");
  }

  public static String getJQuerySelector(By seleniumSelector) {
    return "$(\"" + getJQuerySelectorString(seleniumSelector) + "\")";
  }

  public static String getJQuerySelectorString(By seleniumSelector) {
    if (seleniumSelector instanceof By.ByName) {
      String name = seleniumSelector.toString().replaceFirst("By\\.name:\\s*(.*)", "$1");
      return "*[name='" + name + "']";
    } else if (seleniumSelector instanceof By.ById) {
      String id = seleniumSelector.toString().replaceFirst("By\\.id:\\s*(.*)", "$1");
      return "#" + id;
    } else if (seleniumSelector instanceof By.ByClassName) {
      String className = seleniumSelector.toString().replaceFirst("By\\.className:\\s*(.*)", "$1");
      return "." + className;
    } else if (seleniumSelector instanceof By.ByXPath) {
      String seleniumXPath = seleniumSelector.toString().replaceFirst("By\\.xpath:\\s*(.*)", "$1");
      return seleniumXPath.replaceFirst("\\/\\/(.*)", "$1").replaceAll("\\[@", "[");
    }

    return seleniumSelector.toString();
  }

  public static String describeElement(WebElement element) {
    return "<" + element.getTagName() +
        " value=" + element.getAttribute("value") +
        " class=" + element.getAttribute("class") +
        " id=" + element.getAttribute("id") +
        " name=" + element.getAttribute("name") +
        " onclick=" + element.getAttribute("onclick") +
        " onClick=" + element.getAttribute("onClick") +
        " onchange=" + element.getAttribute("onchange") +
        " onChange=" + element.getAttribute("onChange") +
        ">" + element.getText() +
        "</" + element.getTagName() + ">";
  }

  protected static Object executeJavaScript(String jsCode) {
    return ((JavascriptExecutor) webdriver).executeScript(jsCode);
  }

  /**
   * It works only if jQuery "scroll" plugin is included in page being tested
   *
   * @param element HTML element to scroll to.
   */
  protected void scrollTo(By element) {
    executeJavaScript("$.scrollTo('" + getJQuerySelectorString(element) + "')");
  }

  protected static void selectRadio(String radioFieldId, String value) {
    String radioButtonId = radioFieldId + value;

    assertThat(webdriver.findElements(By.id(radioButtonId)).size(), equalTo(1));
    assertThat(getElement(By.id(radioButtonId)).isDisplayed(), is(true));

    By byXpath = By.xpath("//label[@for='" + radioButtonId + "']");
    assertThat(webdriver.findElements(byXpath).size(), equalTo(1));
    assertThat(getElement(byXpath).isDisplayed(), is(true));

    executeJavaScript(getJQuerySelector(By.id(radioButtonId)) + ".attr('checked', true);");
    sleep(100);
    executeJavaScript(getJQuerySelector(By.id(radioButtonId)) + ".click();");
    sleep(100);

//    This doesn't always work properly in Windows:
//    click(By.id(radioFieldId + value));
//    triggerChangeEvent(By.id(radioFieldId));
  }

    public static void selectOption(By selectField, String value) {
    waitFor(selectField, hasOptions());
    findOptionByValue(selectField, value).click();
    triggerChangeEvent(selectField);
  }

  private static WebElement findOptionByValue(By selectField, String value) {
    try {
      WebElement selectElement = getElement(selectField);
      List<WebElement> options = selectElement.findElements(By.tagName("option"));
      for (WebElement option : options) {
        if (option.getAttribute("value").equals(value)) {
          return option;
        }
      }
    } catch (WebDriverException e) {
      throw new IllegalArgumentException("Option " + value + " is not found for select " + selectField, e);
    }
    throw new IllegalArgumentException("Option " + value + " is not found for select " + selectField);
  }

  protected void selectOptionByText(By selectField, String value) {
    waitFor(selectField, hasOptions());
    findOptionByText(selectField, value).click();
    triggerChangeEvent(selectField);
  }

  private static WebElement findOptionByText(By selectField, String text) {
    WebElement selectElement = getElement(selectField);
    try {
      return selectElement.findElement(By.xpath("option[text()='" + text + "']"));
    } catch (WebDriverException e) {
      throw new IllegalArgumentException("Option " + text + " is not found for select " + selectField, e);
    }
  }

  protected static boolean existsAndVisible(By logoutLink) {
    try {
      return webdriver.findElement(logoutLink).isDisplayed();
    } catch (NoSuchElementException doesNotExist) {
      return false;
    }
  }

  protected void followLink(By by) {
    WebElement link = getElement(by);
    String href = link.getAttribute("href");
    link.click();

    // JavaScript $.click() doesn't take effect for <a href>
    if (href != null) {
      navigateToAbsoluteUrl(href);
    }
  }

  public static WebElement assertElement(By selector, Condition condition) {
    WebElement element = getElement(selector);
    if (!condition.apply(element)) {
      fail("Element " + selector + " hasn't " + condition + "; actual value is '" + condition.actualValue(element) + "'");
    }
    return element;
  }

  public static WebElement waitFor(By by, Condition condition) {
    return waitFor(by, condition, defaultWaitingTimeout);
  }

  public static WebElement waitFor(By by, Condition condition, long milliseconds) {
    final long startTime = System.currentTimeMillis();
    WebElement element = null;
    do {
      try {
        element = webdriver.findElement(by);
        if (condition.apply(element)) {
          return element;
        }
      } catch (NoSuchElementException ignore) {
        if (condition.applyNull()) {
          return null;
        }
      }
      sleep(50);
    }
    while (System.currentTimeMillis() - startTime < milliseconds);

    fail("Element " + by + " hasn't " + condition + " in " + milliseconds + " ms.; actual value is '" + condition.actualValue(element) + "'");
    return null;
  }


}
