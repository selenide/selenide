package com.codeborne.selenide;

import com.codeborne.selenide.drivercommands.LazyDriver;
import com.codeborne.selenide.drivercommands.Navigator;
import com.codeborne.selenide.drivercommands.WebDriverWrapper;
import com.codeborne.selenide.ex.JavaScriptErrorsFound;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.JavascriptErrorsCollector;
import com.codeborne.selenide.impl.SelenidePageFactory;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.impl.WebElementWrapper.wrap;
import static java.util.Collections.emptyList;

public class SelenideDriver {
  private final Navigator navigator = new Navigator();
  private static SelenidePageFactory pageFactory = new SelenidePageFactory();
  private static JavascriptErrorsCollector javascriptErrorsCollector = new JavascriptErrorsCollector();
  private static DownloadFileWithHttpRequest downloadFileWithHttpRequest = new DownloadFileWithHttpRequest();

  private final Driver driver;

  public SelenideDriver(Proxy userProvidedProxy, List<WebDriverEventListener> listeners) {
    this.driver = new LazyDriver(userProvidedProxy, listeners);
  }

  public SelenideDriver() {
    this(null, emptyList());
  }

  public SelenideDriver(WebDriver webDriver) {
    this.driver = new WebDriverWrapper(webDriver);
  }

  Driver driver() {
    return driver;
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
    return pageFactory.page(driver(), pageObjectClass);
  }

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
    driver().executeJavaScript("window.location.hash='" + localHash + "'");
  }

  public Browser browser() {
    return driver().browser();
  }

  public SelenideProxyServer getProxy() {
    return driver().getProxy();
  }

  public boolean hasWebDriverStarted() {
    return driver().hasWebDriverStarted();
  }

  public WebDriver getWebDriver() {
    return driver.getWebDriver();
  }

  public WebDriver getAndCheckWebDriver() {
    return driver.getAndCheckWebDriver();
  }

  public void clearCookies() {
    if (driver().hasWebDriverStarted()) {
      driver().getWebDriver().manage().deleteAllCookies();
    }
  }

  public void close() {
    driver.close();
  }

  public WebElement getFocusedElement() {
    return (WebElement) driver.executeJavaScript("return document.activeElement");
  }


  public SelenideWait Wait() {
    return new SelenideWait(getWebDriver());
  }


  public List<String> getJavascriptErrors() {
    return javascriptErrorsCollector.getJavascriptErrors(driver());
  }

  public void assertNoJavascriptErrors() throws JavaScriptErrorsFound {
    List<String> jsErrors = getJavascriptErrors();
    if (jsErrors != null && !jsErrors.isEmpty()) {
      throw new JavaScriptErrorsFound(jsErrors);
    }
  }

  public void zoom(double factor) {
    driver().executeJavaScript(
      "document.body.style.transform = 'scale(' + arguments[0] + ')';" +
        "document.body.style.transformOrigin = '0 0';",
      factor
    );
  }

  public String title() {
    return getWebDriver().getTitle();
  }

  public SelenideElement $(WebElement webElement) {
    return wrap(driver(), webElement);
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
    return ElementFinder.wrap(driver(), cssSelector, index);
  }

  public SelenideElement find(By criteria) {
    return ElementFinder.wrap(driver(), null, criteria, 0);
  }

  public SelenideElement find(By criteria, int index) {
    return ElementFinder.wrap(driver(), null, criteria, index);
  }

  public ElementsCollection $$(Collection<? extends WebElement> elements) {
    return new ElementsCollection(driver(), elements);
  }

  public ElementsCollection $$(String cssSelector) {
    return new ElementsCollection(driver(), cssSelector);
  }

  public ElementsCollection $$x(String xpathExpression) {
    return $$(By.xpath(xpathExpression));
  }

  public ElementsCollection findAll(By seleniumSelector) {
    return new ElementsCollection(driver(), seleniumSelector);
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
    return new Modal(driver());
  }

  public WebDriverLogs getWebDriverLogs() {
    return new WebDriverLogs(driver());
  }

  public void clearBrowserLocalStorage() {
    driver().executeJavaScript("localStorage.clear();");
  }


  public boolean atBottom() {
    return driver().executeJavaScript("return window.pageYOffset + window.innerHeight >= document.body.scrollHeight");
  }

  public File download(String url) throws IOException {
    return download(url, timeout);
  }

  public File download(String url, long timeoutMs) throws IOException {
    return downloadFileWithHttpRequest.download(driver(), url, timeoutMs);
  }
}
