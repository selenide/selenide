package com.codeborne.selenide;

import com.codeborne.selenide.drivercommands.Navigator;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;

import static java.util.Locale.ROOT;
import static java.util.Objects.requireNonNull;

/**
 * Restores browser state and opens the given page.
 * If needed (and configured), logs in and saves the fresh state to the file.
 */
final class OpenWithBrowserState {
  private static final Logger log = LoggerFactory.getLogger(OpenWithBrowserState.class);
  private static final Map<Path, Lock> locks = new ConcurrentHashMap<>();

  private final SelenideDriver driver;
  private final Navigator navigator = new Navigator();

  OpenWithBrowserState(SelenideDriver driver) {
    this.driver = driver;
  }

  void open(String relativeOrAbsoluteUrl, OpenOptions options) {
    String url = navigator.absoluteUrl(driver.config(), relativeOrAbsoluteUrl);
    if (options.login() == null) {
      restoreAndOpen(url, options.browserState());
    }
    else {
      openOrLogin(url, options);
    }
  }

  private void openOrLogin(String url, OpenOptions options) {
    Path file = requireNonNull(options.file());
    BooleanSupplier loggedInCheck = requireNonNull(options.loggedInCheck());

    SavedState savedState = readIfValid(file);
    if (savedState != null && restoreAndCheck(url, savedState.state(), loggedInCheck)) {
      return;
    }

    Lock lock = locks.computeIfAbsent(file.toAbsolutePath().normalize(), path -> new ReentrantLock());
    lock.lock();
    try {
      SavedState currentState = readIfValid(file);
      if (currentState != null && !currentState.isSameAs(savedState) && restoreAndCheck(url, currentState.state(), loggedInCheck)) {
        return; // another thread has just logged in and saved a fresh state
      }
      SavedState staleState = currentState != null ? currentState : savedState;
      if (staleState != null) {
        clearStaleState(staleState.state().origin());
      }
      requireNonNull(options.login()).run();
      saveStateAfterLogin(url, file);
    }
    finally {
      lock.unlock();
    }

    driver.open(url);
    if (!loggedInCheck.getAsBoolean()) {
      throw new IllegalStateException("User is not logged in right after login (%s). Check the login code and the loggedInIf() check."
        .formatted(file));
    }
  }

  private boolean restoreAndCheck(String url, BrowserState savedState, BooleanSupplier loggedInCheck) {
    if (!tryRestoreAndOpen(url, savedState)) {
      log.warn("Cannot reuse browser state of {}: {} redirected to {}",
        savedState.origin(), landingPage(savedState.origin()), driver.webdriver().currentOrigin());
      return false;
    }
    return loggedInCheck.getAsBoolean();
  }

  private void restoreAndOpen(String url, BrowserState content) {
    if (!tryRestoreAndOpen(url, content)) {
      throw new IllegalStateException("Cannot restore browser state of %s: %s redirected to %s"
        .formatted(content.origin(), landingPage(content.origin()), driver.webdriver().currentOrigin()));
    }
  }

  /**
   * @return false if the browser could not open a page of the state's origin (the landing page redirected elsewhere)
   */
  private boolean tryRestoreAndOpen(String url, BrowserState content) {
    String targetOrigin = originOf(url);
    if (!targetOrigin.equals(content.origin())) {
      throw new IllegalArgumentException("Cannot open %s with browser state of %s: different origin"
        .formatted(url, content.origin()));
    }
    if (!openLandingPage(content.origin())) {
      return false;
    }
    SelenideLogger.run("restoreBrowserState", content.origin(), () -> driver.webdriver().restore(content));
    driver.open(url);
    return true;
  }

  /**
   * Login might finish on another origin (e.g. identity provider).
   * The state must be saved from a page of the target origin: otherwise it cannot be restored later.
   */
  private void saveStateAfterLogin(String url, Path file) {
    String targetOrigin = originOf(url);
    if (!targetOrigin.equals(driver.webdriver().currentOrigin()) && !openLandingPage(targetOrigin)) {
      throw new IllegalStateException("Cannot save browser state of %s after login: %s redirected to %s"
        .formatted(targetOrigin, landingPage(targetOrigin), driver.webdriver().currentOrigin()));
    }
    driver.webdriver().saveBrowserState(file);
  }

  /**
   * Stale state must be cleared on its own origin: the page might have redirected elsewhere
   * (e.g. to the login page of an identity provider, whose cookies we don't want to delete).
   */
  private void clearStaleState(String origin) {
    if (openLandingPage(origin)) {
      driver.webdriver().clearBrowserState();
    }
  }

  /**
   * Open a lightweight page of the given origin: we need it to set cookies and storage before opening the real page.
   * @return true if the browser has actually opened a page of the given origin
   */
  private boolean openLandingPage(String origin) {
    driver.getAndCheckWebDriver().navigate().to(landingPage(origin));
    return origin.equals(driver.webdriver().currentOrigin());
  }

  private static String landingPage(String origin) {
    return origin + "/favicon.ico";
  }

  /**
   * @return null if the file doesn't exist or is not a valid browser state (then we just need to log in again)
   */
  @Nullable
  private static SavedState readIfValid(Path file) {
    if (!Files.exists(file)) {
      return null;
    }
    String json = BrowserState.readFile(file);
    try {
      return new SavedState(json, BrowserState.fromJson(json));
    }
    catch (RuntimeException invalidJson) {
      // Don't log the exception message: it may contain parts of the file, i.e. credentials
      log.warn("Ignoring invalid browser state in {} ({})", file.toAbsolutePath(), invalidJson.getClass().getSimpleName());
      return null;
    }
  }

  /**
   * @param json the file content: compared as a whole, because {@link org.openqa.selenium.Cookie#equals}
   *             ignores cookie attributes like expiry
   */
  private record SavedState(String json, BrowserState state) {
    boolean isSameAs(@Nullable SavedState other) {
      return other != null && json.equals(other.json);
    }
  }

  /**
   * @return origin of the url in the same format as JS {@code location.origin} (default port is omitted)
   */
  static String originOf(String url) {
    try {
      URL parsed = URI.create(url).toURL();
      int port = parsed.getPort();
      boolean defaultPort = port == -1 || port == parsed.getDefaultPort();
      return (parsed.getProtocol() + "://" + parsed.getHost()).toLowerCase(ROOT) + (defaultPort ? "" : ":" + port);
    }
    catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid url: " + url, e);
    }
  }

}
