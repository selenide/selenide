package com.codeborne.selenide;

import com.codeborne.selenide.impl.CiReportUrl;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.MutableCapabilities;

import static com.codeborne.selenide.AssertionMode.STRICT;
import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.SelectorMode.CSS;

public class SelenideConfig implements Config {
  private final PropertiesReader properties = new PropertiesReader("selenide.properties");

  private String browser = getProperty("selenide.browser", CHROME);
  private boolean headless = properties.getBoolean("selenide.headless", false);
  @Nullable
  private String remote = getPropertyOrNull("selenide.remote");
  @Nullable
  private String browserSize = getProperty("selenide.browserSize", "1366x768");
  @Nullable
  private String browserVersion = getPropertyOrNull("selenide.browserVersion");
  @Nullable
  private String browserPosition = getPropertyOrNull("selenide.browserPosition");
  private boolean webdriverLogsEnabled = properties.getBoolean("selenide.webdriverLogsEnabled", false);
  @Nullable
  private String browserBinary = getPropertyOrNull("selenide.browserBinary");
  private String pageLoadStrategy = getProperty("selenide.pageLoadStrategy", "normal");
  private long pageLoadTimeout = Long.parseLong(getProperty("selenide.pageLoadTimeout", "30000"));
  private MutableCapabilities browserCapabilities = new MutableCapabilities();

  private String baseUrl = getProperty("selenide.baseUrl", "http://localhost:8080");
  private long timeout = Long.parseLong(getProperty("selenide.timeout", "4000"));
  private long pollingInterval = Long.parseLong(getProperty("selenide.pollingInterval", "200"));
  /**
   * @deprecated Don't use this setting. It leaves the browser and webdriver open.
   * User can later close the browser manually, but the webdriver leaves running forever.
   */
  @Deprecated
  private boolean holdBrowserOpen = properties.getBoolean("selenide.holdBrowserOpen", false);
  private boolean reopenBrowserOnFail = properties.getBoolean("selenide.reopenBrowserOnFail", true);
  private boolean clickViaJs = properties.getBoolean("selenide.clickViaJs", false);
  private boolean screenshots = properties.getBoolean("selenide.screenshots", true);

  private boolean savePageSource = properties.getBoolean("selenide.savePageSource", true);
  private String reportsFolder = getProperty("selenide.reportsFolder", "build/reports/tests");
  private String downloadsFolder = getProperty("selenide.downloadsFolder", "build/downloads");
  @Nullable
  private String reportsUrl = new CiReportUrl().getReportsUrl(getPropertyOrNull("selenide.reportsUrl"));
  private boolean fastSetValue = properties.getBoolean("selenide.fastSetValue", false);
  private TextCheck textCheck = TextCheck.valueOf(getProperty("selenide.textCheck", TextCheck.PARTIAL_TEXT.name()));
  private SelectorMode selectorMode = SelectorMode.valueOf(getProperty("selenide.selectorMode", CSS.name()));
  private AssertionMode assertionMode = AssertionMode.valueOf(getProperty("selenide.assertionMode", STRICT.name()));
  private FileDownloadMode fileDownload = FileDownloadMode.valueOf(getProperty("selenide.fileDownload", HTTPGET.name()));
  private boolean proxyEnabled = properties.getBoolean("selenide.proxyEnabled", false);
  @Nullable
  private String proxyHost = getPropertyOrNull("selenide.proxyHost");
  private int proxyPort = Integer.parseInt(getProperty("selenide.proxyPort", "0"));
  private long remoteReadTimeout = Long.parseLong(getProperty("selenide.remoteReadTimeout", "90000"));
  private long remoteConnectionTimeout = Long.parseLong(getProperty("selenide.remoteConnectionTimeout", "10000"));

  @Override
  public String baseUrl() {
    return baseUrl;
  }

  @CanIgnoreReturnValue
  public SelenideConfig baseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  @Override
  public long timeout() {
    return timeout;
  }

  @CanIgnoreReturnValue
  public SelenideConfig timeout(long timeout) {
    this.timeout = timeout;
    return this;
  }

  @Override
  public long pollingInterval() {
    return pollingInterval;
  }

  @CanIgnoreReturnValue
  public SelenideConfig pollingInterval(long pollingInterval) {
    this.pollingInterval = pollingInterval;
    return this;
  }

  @Deprecated
  @Override
  public boolean holdBrowserOpen() {
    return holdBrowserOpen;
  }

  @Deprecated
  @CanIgnoreReturnValue
  public SelenideConfig holdBrowserOpen(boolean holdBrowserOpen) {
    this.holdBrowserOpen = holdBrowserOpen;
    return this;
  }

  @Override
  public boolean reopenBrowserOnFail() {
    return reopenBrowserOnFail;
  }

  @CanIgnoreReturnValue
  public SelenideConfig reopenBrowserOnFail(boolean reopenBrowserOnFail) {
    this.reopenBrowserOnFail = reopenBrowserOnFail;
    return this;
  }

  @Override
  public boolean clickViaJs() {
    return clickViaJs;
  }

  @CanIgnoreReturnValue
  public SelenideConfig clickViaJs(boolean clickViaJs) {
    this.clickViaJs = clickViaJs;
    return this;
  }

  @Override
  public boolean screenshots() {
    return screenshots;
  }

  @CanIgnoreReturnValue
  public SelenideConfig screenshots(boolean screenshots) {
    this.screenshots = screenshots;
    return this;
  }

  @Override
  public boolean savePageSource() {
    return savePageSource;
  }

  @CanIgnoreReturnValue
  public SelenideConfig savePageSource(boolean savePageSource) {
    this.savePageSource = savePageSource;
    return this;
  }

  @Override
  public String reportsFolder() {
    return reportsFolder;
  }

  @CanIgnoreReturnValue
  public SelenideConfig reportsFolder(String reportsFolder) {
    this.reportsFolder = reportsFolder;
    return this;
  }

  @Override
  public String downloadsFolder() {
    return downloadsFolder;
  }

  @CanIgnoreReturnValue
  public SelenideConfig downloadsFolder(String downloadsFolder) {
    this.downloadsFolder = downloadsFolder;
    return this;
  }

  @Nullable
  @Override
  public String reportsUrl() {
    return reportsUrl;
  }

  @CanIgnoreReturnValue
  public SelenideConfig reportsUrl(@Nullable String reportsUrl) {
    this.reportsUrl = reportsUrl;
    return this;
  }

  @Override
  public boolean fastSetValue() {
    return fastSetValue;
  }

  @Override
  public TextCheck textCheck() {
    return textCheck;
  }

  @CanIgnoreReturnValue
  public SelenideConfig fastSetValue(boolean fastSetValue) {
    this.fastSetValue = fastSetValue;
    return this;
  }

  @CanIgnoreReturnValue
  public SelenideConfig textCheck(TextCheck textCheck) {
    this.textCheck = textCheck;
    return this;
  }

  @Override
  public SelectorMode selectorMode() {
    return selectorMode;
  }

  @CanIgnoreReturnValue
  public SelenideConfig selectorMode(SelectorMode selectorMode) {
    this.selectorMode = selectorMode;
    return this;
  }

  @Override
  public AssertionMode assertionMode() {
    return assertionMode;
  }

  @CanIgnoreReturnValue
  public SelenideConfig assertionMode(AssertionMode assertionMode) {
    this.assertionMode = assertionMode;
    return this;
  }

  @Override
  public FileDownloadMode fileDownload() {
    return fileDownload;
  }

  @CanIgnoreReturnValue
  public SelenideConfig fileDownload(FileDownloadMode fileDownload) {
    this.fileDownload = fileDownload;
    return this;
  }

  @Override
  public boolean proxyEnabled() {
    return proxyEnabled;
  }

  @CanIgnoreReturnValue
  public SelenideConfig proxyEnabled(boolean proxyEnabled) {
    this.proxyEnabled = proxyEnabled;
    return this;
  }

  @Nullable
  @Override
  public String proxyHost() {
    return proxyHost;
  }

  @CanIgnoreReturnValue
  public SelenideConfig proxyHost(@Nullable String proxyHost) {
    this.proxyHost = proxyHost;
    return this;
  }

  @Override
  public int proxyPort() {
    return proxyPort;
  }

  @CanIgnoreReturnValue
  public SelenideConfig proxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
    return this;
  }

  @Override
  public String browser() {
    return browser;
  }

  @CanIgnoreReturnValue
  public SelenideConfig browser(String browser) {
    this.browser = browser;
    return this;
  }

  @Override
  public boolean headless() {
    return headless;
  }

  @CanIgnoreReturnValue
  public SelenideConfig headless(boolean headless) {
    this.headless = headless;
    return this;
  }

  @Nullable
  @Override
  public String remote() {
    return remote;
  }

  @CanIgnoreReturnValue
  public SelenideConfig remote(@Nullable String remote) {
    this.remote = remote;
    return this;
  }

  @Nullable
  @Override
  public String browserSize() {
    return browserSize;
  }

  @CanIgnoreReturnValue
  public SelenideConfig browserSize(@Nullable String browserSize) {
    this.browserSize = browserSize;
    return this;
  }

  @Nullable
  @Override
  public String browserVersion() {
    return browserVersion;
  }

  @CanIgnoreReturnValue
  public SelenideConfig browserVersion(@Nullable String browserVersion) {
    this.browserVersion = browserVersion;
    return this;
  }

  @Nullable
  @Override
  public String browserPosition() {
    return browserPosition;
  }

  @CanIgnoreReturnValue
  public SelenideConfig browserPosition(@Nullable String browserPosition) {
    this.browserPosition = browserPosition;
    return this;
  }

  @Override
  public boolean webdriverLogsEnabled() {
    return webdriverLogsEnabled;
  }

  @CanIgnoreReturnValue
  public SelenideConfig webdriverLogsEnabled(boolean webdriverLogsEnabled) {
    this.webdriverLogsEnabled = webdriverLogsEnabled;
    return this;
  }

  @Nullable
  @Override
  public String browserBinary() {
    return browserBinary;
  }

  @CanIgnoreReturnValue
  public SelenideConfig browserBinary(@Nullable String browserBinary) {
    this.browserBinary = browserBinary;
    return this;
  }

  @Override
  public String pageLoadStrategy() {
    return pageLoadStrategy;
  }

  @Override
  public long pageLoadTimeout() {
    return pageLoadTimeout;
  }

  @CanIgnoreReturnValue
  public SelenideConfig pageLoadStrategy(String pageLoadStrategy) {
    this.pageLoadStrategy = pageLoadStrategy;
    return this;
  }

  @CanIgnoreReturnValue
  public SelenideConfig pageLoadTimeout(long pageLoadTimeout) {
    this.pageLoadTimeout = pageLoadTimeout;
    return this;
  }

  @Override
  public MutableCapabilities browserCapabilities() {
    return browserCapabilities;
  }

  @CanIgnoreReturnValue
  public SelenideConfig browserCapabilities(MutableCapabilities browserCapabilities) {
    this.browserCapabilities = browserCapabilities;
    return this;
  }

  @Nullable
  private String getPropertyOrNull(String key) {
    return properties.getPropertyOrNull(key);
  }

  private String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  @Override
  public long remoteReadTimeout() {
    return remoteReadTimeout;
  }

  @CanIgnoreReturnValue
  public SelenideConfig remoteReadTimeout(long remoteReadTimeout) {
    this.remoteReadTimeout = remoteReadTimeout;
    return this;
  }

  @Override
  public long remoteConnectionTimeout() {
    return remoteConnectionTimeout;
  }

  @CanIgnoreReturnValue
  public SelenideConfig remoteConnectionTimeout(long remoteConnectionTimeout) {
    this.remoteConnectionTimeout = remoteConnectionTimeout;
    return this;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
