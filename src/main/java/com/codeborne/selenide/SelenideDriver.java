package com.codeborne.selenide;

import com.codeborne.selenide.drivercommands.BrowserHealthChecker;
import com.codeborne.selenide.drivercommands.CloseDriverCommand;
import com.codeborne.selenide.drivercommands.Navigator;
import com.codeborne.selenide.drivercommands.SelenideDriverFinalCleanupThread;
import com.codeborne.selenide.ex.JavaScriptErrorsFound;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.JavascriptErrorsCollector;
import com.codeborne.selenide.impl.SelenidePageFactory;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.holdBrowserOpen;
import static com.codeborne.selenide.Configuration.reopenBrowserOnFail;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.impl.WebElementWrapper.wrap;
import static java.lang.Thread.currentThread;
import static java.util.Collections.emptyList;

public class SelenideDriver implements Context {
  private static final Logger log = Logger.getLogger(SelenideDriver.class.getName());

  private final Navigator navigator = new Navigator();
  private final WebDriverFactory factory;
  private final BrowserHealthChecker browserHealthChecker;
  private static SelenidePageFactory pageFactory = new SelenidePageFactory();
  private static JavascriptErrorsCollector javascriptErrorsCollector = new JavascriptErrorsCollector();
  private static DownloadFileWithHttpRequest downloadFileWithHttpRequest = new DownloadFileWithHttpRequest();

  private final Browser browser = new Browser(Configuration.browser, Configuration.headless);

  // TODO split to 2 different classes:

  // Class 1:
  private final Proxy userProvidedProxy;
  private final List<WebDriverEventListener> listeners = new ArrayList<>();
  private SelenideProxyServer selenideProxyServer;

  // Class 2:
  private WebDriver webDriver;

  SelenideDriver(Proxy userProvidedProxy, List<WebDriverEventListener> listeners,
                 WebDriverFactory factory, BrowserHealthChecker browserHealthChecker) {
    this.userProvidedProxy = userProvidedProxy;
    this.listeners.addAll(listeners);
    this.factory = factory;
    this.browserHealthChecker = browserHealthChecker;
    Runtime.getRuntime().addShutdownHook(new SelenideDriverFinalCleanupThread(this));
  }

  public SelenideDriver() {
    this(null, emptyList(), new WebDriverFactory(), new BrowserHealthChecker());
  }

  public SelenideDriver(Proxy userProvidedProxy, List<WebDriverEventListener> listeners, WebDriverFactory factory) {
    this(userProvidedProxy, listeners, factory, new BrowserHealthChecker());
  }

  public SelenideDriver(WebDriver webDriver, WebDriverFactory factory) {
    this(webDriver, factory, new BrowserHealthChecker());
  }

  SelenideDriver(WebDriver webDriver, WebDriverFactory factory, BrowserHealthChecker browserHealthChecker) {
    this(null, emptyList(), factory, browserHealthChecker);
    this.webDriver = webDriver;
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

  public <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl,
                                                Class<PageObjectClass> pageObjectClassClass) {
    return open(relativeOrAbsoluteUrl, "", "", "", pageObjectClassClass);
  }

  public <PageObjectClass> PageObjectClass open(URL absoluteUrl,
                                                Class<PageObjectClass> pageObjectClassClass) {
    return open(absoluteUrl, "", "", "", pageObjectClassClass);
  }

  public <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl,
                                                String domain, String login, String password,
                                                Class<PageObjectClass> pageObjectClassClass) {
    open(relativeOrAbsoluteUrl, domain, login, password);
    return page(pageObjectClassClass);
  }

  public <PageObjectClass> PageObjectClass open(URL absoluteUrl, String domain, String login, String password,
                                                Class<PageObjectClass> pageObjectClassClass) {
    open(absoluteUrl, domain, login, password);
    return page(pageObjectClassClass);
  }

  public <PageObjectClass> PageObjectClass page(Class<PageObjectClass> pageObjectClass) {
    return pageFactory.page(this, pageObjectClass);
  }

  public <PageObjectClass, T extends PageObjectClass> PageObjectClass page(T pageObject) {
    return pageFactory.page(this, pageObject);
  }

  public void refresh() {
    navigator.refresh(this);
  }

  public void back() {
    navigator.back(this);
  }

  public void forward() {
    navigator.forward(this);
  }

  public void updateHash(String hash) {
    String localHash = (hash.charAt(0) == '#') ? hash.substring(1) : hash;
    executeJavaScript("window.location.hash='" + localHash + "'");
  }

  public Browser getBrowser() {
    return browser;
  }

  public boolean hasWebDriverStarted() {
    return webDriver != null;
  }

  public SelenideProxyServer getProxy() {
    return selenideProxyServer;
  }

  public synchronized WebDriver getWebDriver() {
    if (webDriver == null) {
      log.info("No webdriver is bound to current thread: " + currentThread().getId() + " - let's create a new webdriver");
      webDriver = createDriver();
    }
    return webDriver;
  }

  public WebDriver getAndCheckWebDriver() {
    if (webDriver != null && reopenBrowserOnFail && !browserHealthChecker.isBrowserStillOpen(webDriver)) {
      log.info("Webdriver has been closed meanwhile. Let's re-create it.");
      close();
    }
    return getWebDriver();
  }

  WebDriver createDriver() {
    if (!reopenBrowserOnFail) {
      throw new IllegalStateException("No webdriver is bound to current thread: " + currentThread().getId() +
        ", and cannot create a new webdriver because Configuration.reopenBrowserOnFail=false");
    }

    Proxy browserProxy = userProvidedProxy;

    if (Configuration.proxyEnabled) {
      selenideProxyServer = new SelenideProxyServer(userProvidedProxy);
      selenideProxyServer.start();
      browserProxy = selenideProxyServer.createSeleniumProxy();
    }

    WebDriver webdriver = factory.createWebDriver(browserProxy);

    log.info("Create webdriver in current thread " + currentThread().getId() + ": " +
      webdriver.getClass().getSimpleName() + " -> " + webdriver);

    return addListeners(webdriver);
  }

  private WebDriver addListeners(WebDriver webdriver) {
    if (listeners.isEmpty()) {
      return webdriver;
    }

    EventFiringWebDriver wrapper = new EventFiringWebDriver(webdriver);
    for (WebDriverEventListener listener : listeners) {
      log.info("Add listener to webdriver: " + listener);
      wrapper.register(listener);
    }
    return wrapper;
  }

  public void clearCookies() {
    if (webDriver != null) {
      webDriver.manage().deleteAllCookies();
    }
  }

  public void close() {
    if (!holdBrowserOpen) {
      new CloseDriverCommand(webDriver, selenideProxyServer).run();
      webDriver = null;
      selenideProxyServer = null;
    }
  }

  public boolean supportsJavascript() {
    return hasWebDriverStarted() && getWebDriver() instanceof JavascriptExecutor;
  }

  @SuppressWarnings("unchecked")
  public <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) getWebDriver()).executeScript(jsCode, arguments);
  }

  public WebElement getFocusedElement() {
    return (WebElement) executeJavaScript("return document.activeElement");
  }

  public SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(getWebDriver());
  }

  public SelenideWait Wait() {
    return new SelenideWait(getWebDriver());
  }

  public Actions actions() {
    return new Actions(getWebDriver());
  }

  public List<String> getJavascriptErrors() {
    return javascriptErrorsCollector.getJavascriptErrors(this);
  }

  public void assertNoJavascriptErrors() throws JavaScriptErrorsFound {
    List<String> jsErrors = getJavascriptErrors();
    if (jsErrors != null && !jsErrors.isEmpty()) {
      throw new JavaScriptErrorsFound(jsErrors);
    }
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

  public SelenideElement $(WebElement webElement) {
    return wrap(this, webElement);
  }

  public SelenideElement $(String cssSelector) {
    return find(cssSelector);
  }

  public SelenideElement find(String cssSelector) {
    return find(By.cssSelector(cssSelector));
  }

  public SelenideElement $x(String xpathExpression) {
    return find(By.xpath(xpathExpression));
  }

  public SelenideElement $(By seleniumSelector) {
    return find(seleniumSelector);
  }

  public SelenideElement $(By seleniumSelector, int index) {
    return find(seleniumSelector, index);
  }

  public SelenideElement $(String cssSelector, int index) {
    return ElementFinder.wrap(this, cssSelector, index);
  }

  public SelenideElement find(By criteria) {
    return ElementFinder.wrap(this, null, criteria, 0);
  }

  public SelenideElement find(By criteria, int index) {
    return ElementFinder.wrap(this, null, criteria, index);
  }

  public ElementsCollection $$(Collection<? extends WebElement> elements) {
    return new ElementsCollection(this, elements);
  }

  public ElementsCollection $$(String cssSelector) {
    return new ElementsCollection(this, cssSelector);
  }

  public ElementsCollection $$x(String xpathExpression) {
    return $$(By.xpath(xpathExpression));
  }

  public ElementsCollection findAll(By seleniumSelector) {
    return new ElementsCollection(this, seleniumSelector);
  }

  public ElementsCollection $$(By criteria) {
    return findAll(criteria);
  }

  public SelenideElement getSelectedRadio(By radioField) {
    for (WebElement radio : $$(radioField)) {
      if (radio.getAttribute("checked") != null) {
        return $(radio);
      }
    }
    return null;
  }

  public Modal modal() {
    return new Modal(this);
  }

  public WebDriverLogs getWebDriverLogs() {
    return new WebDriverLogs(this);
  }

  public void clearBrowserLocalStorage() {
    executeJavaScript("localStorage.clear();");
  }

  public String getUserAgent() {
    return executeJavaScript("return navigator.userAgent;");
  }

  public boolean atBottom() {
    return executeJavaScript("return window.pageYOffset + window.innerHeight >= document.body.scrollHeight");
  }

  public File download(String url) throws IOException {
    return download(url, timeout);
  }

  public File download(String url, long timeoutMs) throws IOException {
    return downloadFileWithHttpRequest.download(this, url, timeoutMs);
  }
}
