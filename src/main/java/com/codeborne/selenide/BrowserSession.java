package com.codeborne.selenide;

import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.requireNonNull;

/**
 * Current browser session.
 * <p>
 * Allows checking conditions on webdriver (e.g. {@code webdriver().shouldHave(url(...))})
 * and saving/restoring the browser state.
 * </p>
 */
public final class BrowserSession implements Conditional<WebDriver> {
  private final Driver driver;

  BrowserSession(Driver driver) {
    this.driver = driver;
  }

  @Override
  public Driver driver() {
    return driver;
  }

  @Override
  public WebDriver object() {
    return driver.getWebDriver();
  }

  /**
   * Read the state of the current page: its origin, cookies, localStorage and sessionStorage.
   * <p>
   * NB! Only cookies visible to the current page are included (as returned by {@link WebDriver.Options#getCookies()}).
   * </p>
   * @since 7.19.0
   */
  public BrowserState getBrowserState() {
    Map<String, Object> storage = requireNonNull(driver.executeJavaScript("""
      const items = (storage) => Object.keys(storage).reduce((result, key) => {
        result[key] = storage.getItem(key);
        return result;
      }, {});
      return {origin: location.origin, localStorage: items(localStorage), sessionStorage: items(sessionStorage)};"""));
    String origin = (String) storage.get("origin");
    if (origin == null || "null".equals(origin)) {
      throw new IllegalStateException("Cannot read browser state: open a page first (current url: " + driver.url() + ")");
    }
    return new BrowserState(
      origin,
      new ArrayList<>(driver.getWebDriver().manage().getCookies()),
      castMap(storage.get("localStorage")),
      castMap(storage.get("sessionStorage"))
    );
  }

  @SuppressWarnings("unchecked")
  private static Map<String, String> castMap(Object map) {
    return (Map<String, String>) map;
  }

  /**
   * Save the state of the current page (its origin, cookies, localStorage and sessionStorage) to the given file.
   * Later it can be restored with {@link SelenideDriver#open(String, OpenOptions)}
   * or {@link #restoreBrowserState(BrowserState)}.
   *
   * @see #getBrowserState()
   * @since 7.19.0
   */
  public void saveBrowserState(Path file) {
    SelenideLogger.run("saveBrowserState", file.toString(), () -> writeAtomically(file, getBrowserState().toJson()));
  }

  /**
   * Add cookies, localStorage and sessionStorage items from the given state to the current page.
   * <p>
   * Does not navigate anywhere: the current page must belong to the same origin as the state.
   * Existing cookies and storage items are kept (unless overwritten by the state).
   * </p>
   * <p>
   * Usually you don't need this method, but {@link SelenideDriver#open(String, OpenOptions)}.
   * </p>
   * @since 7.19.0
   */
  public void restoreBrowserState(BrowserState state) {
    SelenideLogger.run("restoreBrowserState", state.origin(), () -> restore(state));
  }

  void restore(BrowserState content) {
    String currentOrigin = currentOrigin();
    if (!content.origin().equals(currentOrigin)) {
      throw new IllegalStateException("Cannot restore browser state of %s while current page is %s. Open a page of %s first."
        .formatted(content.origin(), currentOrigin, content.origin()));
    }
    WebDriver.Options options = driver.getWebDriver().manage();
    for (Cookie cookie : content.cookies()) {
      options.addCookie(normalize(cookie));
    }
    driver.executeJavaScript("""
      for (const [key, value] of Object.entries(arguments[0])) localStorage.setItem(key, value);
      for (const [key, value] of Object.entries(arguments[1])) sessionStorage.setItem(key, value);""",
      content.localStorage(), content.sessionStorage());
  }

  /**
   * Prepare a saved cookie for {@link WebDriver.Options#addCookie(Cookie)}:
   * <ul>
   *   <li>Host-only cookies (domain without leading dot) are added without domain:
   *   then the browser binds them to the current host, exactly as the original cookie was.</li>
   *   <li>"SameSite=None" is removed from non-secure cookies. Firefox reports "None" for cookies set without SameSite,
   *   but rejects adding a non-secure cookie with "SameSite=None".</li>
   * </ul>
   */
  static Cookie normalize(Cookie cookie) {
    boolean hostOnly = cookie.getDomain() != null && !cookie.getDomain().startsWith(".");
    boolean insecureSameSiteNone = "None".equalsIgnoreCase(cookie.getSameSite()) && !cookie.isSecure();
    if (!hostOnly && !insecureSameSiteNone) {
      return cookie;
    }
    Cookie.Builder builder = new Cookie.Builder(cookie.getName(), cookie.getValue())
      .domain(hostOnly ? null : cookie.getDomain())
      .path(cookie.getPath())
      .expiresOn(cookie.getExpiry())
      .isSecure(cookie.isSecure())
      .isHttpOnly(cookie.isHttpOnly());
    if (cookie.getSameSite() != null && !insecureSameSiteNone) {
      builder.sameSite(cookie.getSameSite());
    }
    return builder.build();
  }

  /**
   * Delete cookies, localStorage and sessionStorage of the current page.
   */
  void clearBrowserState() {
    driver.getWebDriver().manage().deleteAllCookies();
    driver.executeJavaScript("localStorage.clear(); sessionStorage.clear();");
  }

  String currentOrigin() {
    return requireNonNull(driver.executeJavaScript("return location.origin"));
  }

  private static void writeAtomically(Path file, String content) {
    try {
      Path directory = file.toAbsolutePath().getParent();
      Files.createDirectories(directory);
      Path tempFile = Files.createTempFile(directory, file.getFileName().toString(), ".tmp");
      try {
        Files.writeString(tempFile, content, UTF_8);
        move(tempFile, file);
      }
      finally {
        Files.deleteIfExists(tempFile);
      }
    }
    catch (IOException e) {
      throw new UncheckedIOException("Failed to save browser state to " + file.toAbsolutePath(), e);
    }
  }

  private static void move(Path source, Path target) throws IOException {
    try {
      Files.move(source, target, ATOMIC_MOVE, REPLACE_EXISTING);
    }
    catch (AtomicMoveNotSupportedException e) {
      Files.move(source, target, REPLACE_EXISTING);
    }
  }
}
