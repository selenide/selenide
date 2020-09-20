package com.codeborne.selenide;

import com.codeborne.selenide.drivercommands.LazyDriver;
import com.codeborne.selenide.drivercommands.Navigator;
import com.codeborne.selenide.drivercommands.WebDriverWrapper;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.SelenidePageFactory;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.files.FileFilters.none;
import static com.codeborne.selenide.impl.WebElementWrapper.wrap;
import static java.util.Collections.emptyList;

/**
 * "Selenide driver" is a container for WebDriver + proxy server + settings
 */
@ParametersAreNonnullByDefault
public class SelenideDriver {
  private static final Navigator navigator = new Navigator();
  private static final ScreenShotLaboratory screenshots = ScreenShotLaboratory.getInstance();

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

  public SelenideDriver(Config config, WebDriver webDriver, @Nullable SelenideProxyServer selenideProxy) {
    this(config, webDriver, selenideProxy, new SharedDownloadsFolder(config.downloadsFolder()));
  }

  public SelenideDriver(Config config, WebDriver webDriver, @Nullable SelenideProxyServer selenideProxy,
                        DownloadsFolder browserDownloadsFolder) {
    this.config = config;
    this.driver = new WebDriverWrapper(config, webDriver, selenideProxy, browserDownloadsFolder);
  }

  @CheckReturnValue
  @Nonnull
  public Config config() {
    return config;
  }

  @CheckReturnValue
  @Nonnull
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
  @Nonnull
  public <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl,
                                                Class<PageObjectClass> pageObjectClassClass) {
    return open(relativeOrAbsoluteUrl, "", "", "", pageObjectClassClass);
  }

  @CheckReturnValue
  @Nonnull
  public <PageObjectClass> PageObjectClass open(URL absoluteUrl,
                                                Class<PageObjectClass> pageObjectClassClass) {
    return open(absoluteUrl, "", "", "", pageObjectClassClass);
  }

  @CheckReturnValue
  @Nonnull
  public <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl,
                                                String domain, String login, String password,
                                                Class<PageObjectClass> pageObjectClassClass) {
    open(relativeOrAbsoluteUrl, domain, login, password);
    return page(pageObjectClassClass);
  }

  @CheckReturnValue
  @Nonnull
  public <PageObjectClass> PageObjectClass open(URL absoluteUrl, String domain, String login, String password,
                                                Class<PageObjectClass> pageObjectClassClass) {
    open(absoluteUrl, domain, login, password);
    return page(pageObjectClassClass);
  }

  @CheckReturnValue
  @Nonnull
  public <PageObjectClass> PageObjectClass page(Class<PageObjectClass> pageObjectClass) {
    return pageFactory().page(driver(), pageObjectClass);
  }

  @CheckReturnValue
  @Nonnull
  public <PageObjectClass, T extends PageObjectClass> PageObjectClass page(T pageObject) {
    return pageFactory().page(driver(), pageObject);
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
  @Nonnull
  public Browser browser() {
    return driver().browser();
  }

  @CheckReturnValue
  @Nullable
  public SelenideProxyServer getProxy() {
    return driver().getProxy();
  }

  public boolean hasWebDriverStarted() {
    return driver().hasWebDriverStarted();
  }

  @CheckReturnValue
  @Nonnull
  public WebDriver getWebDriver() {
    return driver.getWebDriver();
  }

  @CheckReturnValue
  @Nonnull
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
  @Nullable
  public WebElement getFocusedElement() {
    return executeJavaScript("return document.activeElement");
  }

  @CheckReturnValue
  @Nonnull
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

  @Nullable
  public String title() {
    return getWebDriver().getTitle();
  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement $(WebElement webElement) {
    return wrap(driver(), webElement);
  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement $(String cssSelector) {
    return find(cssSelector);
  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement find(String cssSelector) {
    return find(By.cssSelector(cssSelector));
  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement $x(String xpathExpression) {
    return find(By.xpath(xpathExpression));
  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement $(By seleniumSelector) {
    return find(seleniumSelector);
  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement $(By seleniumSelector, int index) {
    return find(seleniumSelector, index);
  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement $(String cssSelector, int index) {
    return ElementFinder.wrap(driver(), cssSelector, index);
  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement find(By criteria) {
    return ElementFinder.wrap(driver(), null, criteria, 0);
  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement find(By criteria, int index) {
    return ElementFinder.wrap(driver(), null, criteria, index);
  }

  @CheckReturnValue
  @Nonnull
  public ElementsCollection $$(Collection<? extends WebElement> elements) {
    return new ElementsCollection(driver(), elements);
  }

  @CheckReturnValue
  @Nonnull
  public ElementsCollection $$(String cssSelector) {
    return new ElementsCollection(driver(), cssSelector);
  }

  @CheckReturnValue
  @Nonnull
  public ElementsCollection $$x(String xpathExpression) {
    return $$(By.xpath(xpathExpression));
  }

  @CheckReturnValue
  @Nonnull
  public ElementsCollection findAll(By seleniumSelector) {
    return new ElementsCollection(driver(), seleniumSelector);
  }

  @CheckReturnValue
  @Nonnull
  public ElementsCollection findAll(String cssSelector) {
    return new ElementsCollection(driver(), By.cssSelector(cssSelector));
  }

  @CheckReturnValue
  @Nonnull
  public ElementsCollection $$(By criteria) {
    return findAll(criteria);
  }

  @CheckReturnValue
  @Nullable
  public SelenideElement getSelectedRadio(By radioField) {
    for (WebElement radio : $$(radioField)) {
      if (radio.getAttribute("checked") != null) {
        return $(radio);
      }
    }
    return null;
  }

  @CheckReturnValue
  @Nonnull
  public Modal modal() {
    return new Modal(driver());
  }

  @CheckReturnValue
  @Nonnull
  public WebDriverLogs getWebDriverLogs() {
    return new WebDriverLogs(driver());
  }

  public void clearBrowserLocalStorage() {
    executeJavaScript("localStorage.clear();");
  }

  public boolean atBottom() {
    return executeJavaScript("return window.pageYOffset + window.innerHeight >= document.body.scrollHeight");
  }

  @Nonnull
  public SelenideTargetLocator switchTo() {
    return driver().switchTo();
  }

  @CheckReturnValue
  @Nonnull
  public String url() {
    return getWebDriver().getCurrentUrl();
  }

  @CheckReturnValue
  @Nullable
  public String source() {
    return getWebDriver().getPageSource();
  }

  @CheckReturnValue
  @Nonnull
  public String getCurrentFrameUrl() {
    return executeJavaScript("return window.location.href").toString();
  }

  @CheckReturnValue
  @Nonnull
  public String getUserAgent() {
    return driver().getUserAgent();
  }

  /**
   * Take a screenshot of the current page
   *
   * @return absolute path of the screenshot taken or null if failed to create screenshot
   * @since 5.14.0
   */
  @CheckReturnValue
  @Nullable
  public String screenshot(String fileName) {
    return screenshots.takeScreenShot(driver(), fileName);
  }

  /**
   * Take a screenshot of the current page
   *
   * @return The screenshot (as bytes, base64 or temporary file)
   * @since 5.14.0
   */
  @CheckReturnValue
  @Nullable
  public <T> T screenshot(OutputType<T> outputType) {
    return screenshots.takeScreenShot(driver(), outputType);
  }

  @Nonnull
  public File download(String url) throws IOException, URISyntaxException {
    return download(new URI(url), config.timeout());
  }

  @Nonnull
  public File download(String url, long timeoutMs) throws IOException, URISyntaxException {
    return download(new URI(url), timeoutMs);
  }

  @Nonnull
  public File download(URI url) throws IOException {
    return download(url, config.timeout());
  }

  @Nonnull
  public File download(URI url, long timeoutMs) throws IOException {
    return downloadFileWithHttpRequest().download(driver(), url, timeoutMs, none());
  }

  @CheckReturnValue
  @Nonnull
  public LocalStorage getLocalStorage() {
    return new LocalStorage(driver());
  }

  private static SelenidePageFactory pageFactory;
  private static DownloadFileWithHttpRequest downloadFileWithHttpRequest;

  private static synchronized SelenidePageFactory pageFactory() {
    if (pageFactory == null) pageFactory = new SelenidePageFactory();
    return pageFactory;
  }

  private static synchronized DownloadFileWithHttpRequest downloadFileWithHttpRequest() {
    if (downloadFileWithHttpRequest == null) downloadFileWithHttpRequest = new DownloadFileWithHttpRequest();
    return downloadFileWithHttpRequest;
  }
}
