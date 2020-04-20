package com.codeborne.selenide;

import com.codeborne.selenide.drivercommands.LazyDriver;
import com.codeborne.selenide.drivercommands.Navigator;
import com.codeborne.selenide.drivercommands.WebDriverWrapper;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.SelenidePageFactory;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.google.errorprone.annotations.CheckReturnValue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.files.FileFilters.none;
import static com.codeborne.selenide.impl.WebElementWrapper.wrap;
import static java.util.Collections.emptyList;

/**
 * "Selenide driver" is a container for WebDriver + proxy server + settings
 */
public class SelenideDriver {
  private static final Navigator navigator = new Navigator();
  private static final SelenidePageFactory pageFactory = new SelenidePageFactory();
  private static final DownloadFileWithHttpRequest downloadFileWithHttpRequest = new DownloadFileWithHttpRequest();

  private final Config config;
  private final Driver driver;

  public SelenideDriver(Config config) {
    this(config, emptyList());
  }

  public SelenideDriver(Config config, List<WebDriverEventListener> listeners) {
    this(config, new LazyDriver(config, null, listeners));
  }

  public SelenideDriver(Config config, Driver driver) {
    this.config = config;
    this.driver = driver;
  }

  public SelenideDriver(Config config, WebDriver webDriver, SelenideProxyServer selenideProxy) {
    this.config = config;
    this.driver = new WebDriverWrapper(config, webDriver, selenideProxy);
  }

  @CheckReturnValue
  public Config config() {
    return config;
  }

  @CheckReturnValue
  public Driver driver() {
    return driver;
  }

  public void open() {
    navigator.open(this);
  }

  public void open(String relativeOrAbsoluteUrl) {
    navigator.open(this, relativeOrAbsoluteUrl);
  }

  public void open(URL absoluteUrl) {
    navigator.open(this, absoluteUrl);
  }

  public void open(String relativeOrAbsoluteUrl, String domain, String login, String password) {
    navigator.open(this, relativeOrAbsoluteUrl, domain, login, password);
  }

  public void open(String relativeOrAbsoluteUrl, AuthenticationType authenticationType, Credentials credentials) {
    navigator.open(this, relativeOrAbsoluteUrl, authenticationType, credentials);
  }

  public void open(URL absoluteUrl, String domain, String login, String password) {
    navigator.open(this, absoluteUrl, domain, login, password);
  }

  @CheckReturnValue
  public <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl,
                                                Class<PageObjectClass> pageObjectClassClass) {
    return open(relativeOrAbsoluteUrl, "", "", "", pageObjectClassClass);
  }

  @CheckReturnValue
  public <PageObjectClass> PageObjectClass open(URL absoluteUrl,
                                                Class<PageObjectClass> pageObjectClassClass) {
    return open(absoluteUrl, "", "", "", pageObjectClassClass);
  }

  @CheckReturnValue
  public <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl,
                                                String domain, String login, String password,
                                                Class<PageObjectClass> pageObjectClassClass) {
    open(relativeOrAbsoluteUrl, domain, login, password);
    return page(pageObjectClassClass);
  }

  @CheckReturnValue
  public <PageObjectClass> PageObjectClass open(URL absoluteUrl, String domain, String login, String password,
                                                Class<PageObjectClass> pageObjectClassClass) {
    open(absoluteUrl, domain, login, password);
    return page(pageObjectClassClass);
  }

  @CheckReturnValue
  public <PageObjectClass> PageObjectClass page(Class<PageObjectClass> pageObjectClass) {
    return pageFactory.page(driver(), pageObjectClass);
  }

  @CheckReturnValue
  public <PageObjectClass, T extends PageObjectClass> PageObjectClass page(T pageObject) {
    return pageFactory.page(driver(), pageObject);
  }

  public void refresh() {
    navigator.refresh(driver());
  }

  public void back() {
    navigator.back(driver());
  }

  public void forward() {
    navigator.forward(driver());
  }

  public void updateHash(String hash) {
    String localHash = (hash.charAt(0) == '#') ? hash.substring(1) : hash;
    executeJavaScript("window.location.hash='" + localHash + "'");
  }

  @CheckReturnValue
  public Browser browser() {
    return driver().browser();
  }

  @CheckReturnValue
  public SelenideProxyServer getProxy() {
    return driver().getProxy();
  }

  public boolean hasWebDriverStarted() {
    return driver().hasWebDriverStarted();
  }

  @CheckReturnValue
  public WebDriver getWebDriver() {
    return driver.getWebDriver();
  }

  @CheckReturnValue
  public WebDriver getAndCheckWebDriver() {
    return driver.getAndCheckWebDriver();
  }

  public void clearCookies() {
    driver().clearCookies();
  }

  public void close() {
    driver.close();
  }

  public <T> T executeJavaScript(String jsCode, Object... arguments) {
    return driver().executeJavaScript(jsCode, arguments);
  }

  public <T> T executeAsyncJavaScript(String jsCode, Object... arguments) {
    return driver().executeAsyncJavaScript(jsCode, arguments);
  }

  @CheckReturnValue
  public WebElement getFocusedElement() {
    return executeJavaScript("return document.activeElement");
  }

  @CheckReturnValue
  public SelenideWait Wait() {
    return new SelenideWait(getWebDriver(), config().timeout(), config().pollingInterval());
  }

  public void zoom(double factor) {
    executeJavaScript(
      "document.body.style.transform = 'scale(' + arguments[0] + ')';" +
        "document.body.style.transformOrigin = '0 0';",
      factor
    );
  }

  public String title() {
    return getWebDriver().getTitle();
  }

  @CheckReturnValue
  public SelenideElement $(WebElement webElement) {
    return wrap(driver(), webElement);
  }

  @CheckReturnValue
  public SelenideElement $(String cssSelector) {
    return find(cssSelector);
  }

  @CheckReturnValue
  public SelenideElement find(String cssSelector) {
    return find(By.cssSelector(cssSelector));
  }

  @CheckReturnValue
  public SelenideElement $x(String xpathExpression) {
    return find(By.xpath(xpathExpression));
  }

  @CheckReturnValue
  public SelenideElement $(By seleniumSelector) {
    return find(seleniumSelector);
  }

  @CheckReturnValue
  public SelenideElement $(By seleniumSelector, int index) {
    return find(seleniumSelector, index);
  }

  @CheckReturnValue
  public SelenideElement $(String cssSelector, int index) {
    return ElementFinder.wrap(driver(), cssSelector, index);
  }

  @CheckReturnValue
  public SelenideElement find(By criteria) {
    return ElementFinder.wrap(driver(), null, criteria, 0);
  }

  @CheckReturnValue
  public SelenideElement find(By criteria, int index) {
    return ElementFinder.wrap(driver(), null, criteria, index);
  }

  @CheckReturnValue
  public ElementsCollection $$(Collection<? extends WebElement> elements) {
    return new ElementsCollection(driver(), elements);
  }

  @CheckReturnValue
  public ElementsCollection $$(String cssSelector) {
    return new ElementsCollection(driver(), cssSelector);
  }

  @CheckReturnValue
  public ElementsCollection $$x(String xpathExpression) {
    return $$(By.xpath(xpathExpression));
  }

  @CheckReturnValue
  public ElementsCollection findAll(By seleniumSelector) {
    return new ElementsCollection(driver(), seleniumSelector);
  }

  @CheckReturnValue
  public ElementsCollection $$(By criteria) {
    return findAll(criteria);
  }

  @CheckReturnValue
  public SelenideElement getSelectedRadio(By radioField) {
    for (WebElement radio : $$(radioField)) {
      if (radio.getAttribute("checked") != null) {
        return $(radio);
      }
    }
    return null;
  }

  @CheckReturnValue
  public Modal modal() {
    return new Modal(driver());
  }

  @CheckReturnValue
  public WebDriverLogs getWebDriverLogs() {
    return new WebDriverLogs(driver());
  }

  public void clearBrowserLocalStorage() {
    executeJavaScript("localStorage.clear();");
  }

  public boolean atBottom() {
    return executeJavaScript("return window.pageYOffset + window.innerHeight >= document.body.scrollHeight");
  }

  public SelenideTargetLocator switchTo() {
    return driver().switchTo();
  }

  @CheckReturnValue
  public String url() {
    return getWebDriver().getCurrentUrl();
  }

  @CheckReturnValue
  public String source() {
    return getWebDriver().getPageSource();
  }

  @CheckReturnValue
  public String getCurrentFrameUrl() {
    return executeJavaScript("return window.location.href").toString();
  }

  @CheckReturnValue
  public String getUserAgent() {
    return driver().getUserAgent();
  }

  public File download(String url) throws IOException {
    return download(url, config.timeout());
  }

  public File download(String url, long timeoutMs) throws IOException {
    return downloadFileWithHttpRequest.download(driver(), url, timeoutMs, none());
  }
}
