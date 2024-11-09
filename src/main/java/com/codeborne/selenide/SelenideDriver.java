package com.codeborne.selenide;

import com.codeborne.selenide.drivercommands.LazyDriver;
import com.codeborne.selenide.drivercommands.Navigator;
import com.codeborne.selenide.drivercommands.WebDriverWrapper;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.Lazy;
import com.codeborne.selenide.impl.PageObjectFactory;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.selector.FocusedElementLocator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.events.WebDriverListener;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.commands.Util.classOf;
import static com.codeborne.selenide.files.FileFilters.none;
import static com.codeborne.selenide.impl.Lazy.lazyEvaluated;
import static com.codeborne.selenide.impl.Plugins.inject;
import static com.codeborne.selenide.impl.WebElementWrapper.wrap;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

/**
 * "Selenide driver" is a container for WebDriver + proxy server + settings
 */
public class SelenideDriver {
  private static final JavaScript zoomJs = new JavaScript("zoom.js");
  private static final Navigator navigator = new Navigator();
  private static final ScreenShotLaboratory screenshots = ScreenShotLaboratory.getInstance();

  private final Config config;
  private final Driver driver;

  public SelenideDriver(Config config) {
    this(config, emptyList());
  }

  public SelenideDriver(Config config, List<WebDriverListener> listeners) {
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

  public Config config() {
    return config;
  }

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

  public <PageObjectClass> PageObjectClass open(String relativeOrAbsoluteUrl,
                                                Class<PageObjectClass> pageObjectClassClass) {
    open(relativeOrAbsoluteUrl);
    return page(pageObjectClassClass);
  }

  public <PageObjectClass> PageObjectClass open(URL absoluteUrl,
                                                Class<PageObjectClass> pageObjectClassClass) {
    open(absoluteUrl);
    return page(pageObjectClassClass);
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

  /**
   * @param reified Don't pass any values here. It's Java Magic :)
   */
  @SafeVarargs
  public final <PageObjectClass> PageObjectClass page(PageObjectClass... reified) {
    return pageFactory.page(driver(), classOf(reified));
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
    SelenideLogger.run("updateHash", hash, () -> {
      String localHash = (hash.charAt(0) == '#') ? hash.substring(1) : hash;
      executeJavaScript("window.location.hash='" + localHash + "'");
    });
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

  @CanIgnoreReturnValue
  public WebDriver getAndCheckWebDriver() {
    return driver.getAndCheckWebDriver();
  }

  public void clearCookies() {
    SelenideLogger.run("clearCookies", "", () -> {
      driver().clearCookies();
    });
  }

  public void close() {
    driver.close();
  }

  @Nullable
  @CanIgnoreReturnValue
  public <T> T executeJavaScript(String jsCode, Object... arguments) {
    return driver().executeJavaScript(jsCode, arguments);
  }

  @Nullable
  @CanIgnoreReturnValue
  public <T> T executeAsyncJavaScript(String jsCode, Object... arguments) {
    return driver().executeAsyncJavaScript(jsCode, arguments);
  }

  public SelenideElement getFocusedElement() {
    return $(new FocusedElementLocator());
  }

  /**
   * Returns selected text or empty string if no text is selected.
   *
   * @return selected text
   */
  public String getSelectedText() {
    return requireNonNull(executeJavaScript("return window.getSelection().toString()"));
  }

  /**
   * Copy selected text or empty string if no text is selected to clipboard.
   *
   * @return the copied text
   *
   * @see #getClipboard()
   * @see Clipboard
   */
  @CanIgnoreReturnValue
  public String copy() {
    String selectedText = this.getSelectedText();
    this.getClipboard().setText(selectedText);
    return selectedText;
  }

  public SelenideWait Wait() {
    return new SelenideWait(getWebDriver(), config().timeout(), config().pollingInterval());
  }

  public void zoom(double factor) {
    zoomJs.execute(driver(), factor);
  }

  @Nullable
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

  public ElementsCollection findAll(String cssSelector) {
    return new ElementsCollection(driver(), By.cssSelector(cssSelector));
  }

  public ElementsCollection $$(By criteria) {
    return findAll(criteria);
  }

  @Nullable
  public SelenideElement getSelectedRadio(By radioField) {
    for (WebElement radio : $$(radioField).asFixedIterable()) {
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
    executeJavaScript("localStorage.clear();");
  }

  public boolean atBottom() {
    return Boolean.TRUE.equals(executeJavaScript("return window.scrollY + window.innerHeight >= document.body.scrollHeight"));
  }

  public SelenideTargetLocator switchTo() {
    return driver().switchTo();
  }

  public String url() {
    return requireNonNull(getWebDriver().getCurrentUrl());
  }

  @Nullable
  public String source() {
    return getWebDriver().getPageSource();
  }

  public String getCurrentFrameUrl() {
    return requireNonNull(executeJavaScript("return window.location.href"));
  }

  public String getUserAgent() {
    return driver().getUserAgent();
  }

  public SessionId getSessionId() {
    return driver().getSessionId();
  }

  /**
   * Take a screenshot of the current page
   *
   * @return absolute path of the screenshot taken or null if failed to create screenshot
   */
  @Nullable
  public String screenshot(String fileName) {
    return screenshots.takeScreenshot(driver(), fileName, true, false).getImage();
  }

  /**
   * Take a screenshot of the current page
   *
   * @return The screenshot (as bytes, base64 or temporary file)
   */
  @Nullable
  public <T> T screenshot(OutputType<T> outputType) {
    return screenshots.takeScreenShot(driver(), outputType);
  }

  public File download(String url) throws URISyntaxException {
    return download(new URI(url), config.timeout());
  }

  public File download(String url, long timeoutMs) throws URISyntaxException {
    return download(new URI(url), timeoutMs);
  }

  public File download(URI url) {
    return download(url, config.timeout());
  }

  public File download(URI url, long timeoutMs) {
    return downloadFileWithHttpRequest().download(driver(), url, timeoutMs, none());
  }

  public LocalStorage getLocalStorage() {
    return new LocalStorage(driver());
  }

  public SessionStorage getSessionStorage() {
    return new SessionStorage(driver());
  }

  public Clipboard getClipboard() {
    return inject(ClipboardService.class).getClipboard(driver());
  }

  private static final PageObjectFactory pageFactory = inject(PageObjectFactory.class);
  private static final Lazy<DownloadFileWithHttpRequest> downloadFileWithHttpRequest = lazyEvaluated(DownloadFileWithHttpRequest::new);

  private static synchronized DownloadFileWithHttpRequest downloadFileWithHttpRequest() {
    return downloadFileWithHttpRequest.get();
  }

  public Conditional<WebDriver> webdriver() {
    return new WebDriverConditional(driver);
  }
}
