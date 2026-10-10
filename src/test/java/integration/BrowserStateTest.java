package integration;

import com.codeborne.selenide.BrowserSession;
import com.codeborne.selenide.BrowserState;
import com.codeborne.selenide.OpenOptions;
import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.Cookie;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static com.codeborne.selenide.OpenOptions.withBrowserState;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

final class BrowserStateTest extends ITest {
  @TempDir
  private Path tempDir;
  private Path stateFile;
  private final AtomicInteger loginCount = new AtomicInteger();

  @BeforeEach
  void setUp() {
    openFile("start_page.html");
    driver().getCookieStore().clear();
    driver().getLocalStorage().clear();
    driver().getSessionStorage().clear();
    stateFile = tempDir.resolve("auth/state.json");
  }

  @Test
  void savesAndRestoresBrowserStateInAnotherBrowser() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    driver().getCookieStore().add("user", "bob");
    driver().getCookieStore().add(new Cookie.Builder("SID", "secret-session").path("/").isHttpOnly(true).build());
    driver().getLocalStorage().setItem("token", "jwt-123");
    driver().getSessionStorage().setItem("tab", "orders");

    driver().webdriver().saveBrowserState(stateFile);
    assertThat(stateFile).exists();

    SelenideDriver anotherBrowser = new SelenideDriver(config());
    try {
      anotherBrowser.open("/empty.html", withBrowserState(stateFile));

      anotherBrowser.webdriver().shouldHave(urlContaining("/empty.html"));
      assertThat(anotherBrowser.getCookieStore().get("user").getValue()).isEqualTo("bob");
      Cookie sessionCookie = anotherBrowser.getCookieStore().get("SID");
      assertThat(sessionCookie.getValue()).isEqualTo("secret-session");
      assertThat(sessionCookie.isHttpOnly()).isTrue();
      assertThat(anotherBrowser.getLocalStorage().getItem("token")).isEqualTo("jwt-123");
      assertThat(anotherBrowser.getSessionStorage().getItem("tab")).isEqualTo("orders");
    }
    finally {
      anotherBrowser.close();
    }
  }

  @Test
  void restoresBrowserStateFromMemory() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    driver().getLocalStorage().setItem("token", "jwt-456");
    BrowserState state = driver().webdriver().getBrowserState();
    driver().getLocalStorage().clear();

    driver().open("/empty.html", withBrowserState(state));

    driver().webdriver().shouldHave(urlContaining("/empty.html"));
    assertThat(driver().getLocalStorage().getItem("token")).isEqualTo("jwt-456");
  }

  @Test
  void getBrowserState() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    driver().getCookieStore().add("user", "bob");
    driver().getLocalStorage().setItem("token", "jwt-123");

    BrowserState state = driver().webdriver().getBrowserState();

    assertThat(state.origin()).isEqualTo(getBaseUrl());
    assertThat(state.cookies()).extracting(Cookie::getName).contains("user");
    assertThat(state.localStorage()).containsEntry("token", "jwt-123");
    assertThat(state.sessionStorage()).isEmpty();
  }

  @Test
  void restoreBrowserState_onCurrentPage() {
    BrowserState state = BrowserState.fromJson(
      "{\"origin\": \"%s\", \"cookies\": [{\"name\": \"user\", \"value\": \"alice\"}], \"localStorage\": {\"token\": \"t-1\"}}"
        .formatted(getBaseUrl()));

    driver().webdriver().restoreBrowserState(state);

    assertThat(driver().getCookieStore().get("user").getValue()).isEqualTo("alice");
    assertThat(driver().getLocalStorage().getItem("token")).isEqualTo("t-1");
  }

  @Test
  void restoreBrowserState_failsOnPageOfAnotherOrigin() {
    BrowserState state = BrowserState.fromJson("{\"origin\": \"https://another.example.com\"}");

    BrowserSession browserSession = driver().webdriver();

    assertThatThrownBy(() -> browserSession.restoreBrowserState(state))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Cannot restore browser state of https://another.example.com while current page is %s. " +
        "Open a page of https://another.example.com first.", getBaseUrl());
  }

  @Test
  void open_failsForUrlOfAnotherOrigin() {
    OpenOptions options = withBrowserState(BrowserState.fromJson("{\"origin\": \"%s\"}".formatted(getBaseUrl())));
    SelenideDriver driver = driver();

    assertThatThrownBy(() -> driver.open("https://another.example.com/dashboard", options))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Cannot open https://another.example.com/dashboard with browser state of %s: different origin", getBaseUrl());
  }

  @Test
  void open_failsIfFileDoesNotExist() {
    OpenOptions options = withBrowserState(stateFile);
    SelenideDriver driver = driver();

    assertThatThrownBy(() -> driver.open("/empty.html", options))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Browser state file not found: " + stateFile.toAbsolutePath());
  }

  @Test
  void orLogin_logsInAndSavesState_ifFileDoesNotExist() {
    driver().open("/empty.html", authState());

    assertThat(loginCount).hasValue(1);
    assertThat(stateFile).exists();
    assertThat(isLoggedIn().getAsBoolean()).isTrue();
    driver().webdriver().shouldHave(urlContaining("/empty.html"));
  }

  @Test
  void orLogin_reusesSavedState_withoutLogin() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    driver().open("/empty.html", authState());
    driver().getLocalStorage().clear();
    driver().open("/start_page.html");

    driver().open("/empty.html", authState());

    assertThat(loginCount).hasValue(1);
    assertThat(isLoggedIn().getAsBoolean()).isTrue();
    driver().webdriver().shouldHave(urlContaining("/empty.html"));
  }

  @Test
  void orLogin_logsInAgain_ifSavedStateIsNotValidAnymore() throws Exception {
    assumeThat(browser().isHtmlUnit()).isFalse();
    driver().open("/empty.html", authState());
    Files.writeString(stateFile, Files.readString(stateFile, UTF_8).replace("valid-token", "expired-token"), UTF_8);

    driver().open("/empty.html", authState());

    assertThat(loginCount).hasValue(2);
    assertThat(isLoggedIn().getAsBoolean()).isTrue();
    assertThat(Files.readString(stateFile, UTF_8)).contains("valid-token").doesNotContain("expired-token");
  }

  @Test
  void orLogin_clearsStaleStateOnItsOwnOrigin_evenIfPageRedirectedToAnotherOrigin() throws Exception {
    assumeThat(browser().isHtmlUnit()).isFalse();
    driver().open("/empty.html", authState());
    String expiredState = Files.readString(stateFile, UTF_8)
      .replace("valid-token", "expired-token")
      .replace("\"localStorage\": {", "\"localStorage\": {\"stale\": \"value\",");
    assertThat(expiredState).contains("expired-token", "stale");
    Files.writeString(stateFile, expiredState, UTF_8);
    String anotherOrigin = getBaseUrl().replace("127.0.0.1", "localhost");

    driver().open("/empty.html", withBrowserState(stateFile)
      .loggedInIf(() -> {
        boolean loggedIn = isLoggedIn().getAsBoolean();
        if (!loggedIn) {
          driver().open(anotherOrigin + "/start_page.html"); // like a redirect to a login page of identity provider
        }
        return loggedIn;
      })
      .orLogin(this::login));

    assertThat(loginCount).hasValue(2);
    assertThat(isLoggedIn().getAsBoolean()).isTrue();
    assertThat(driver().getLocalStorage().getItem("stale")).isNull();
  }

  @Test
  void orLogin_savesStateOfTargetOrigin_evenIfLoginFinishedOnAnotherOrigin() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    String anotherOrigin = getBaseUrl().replace("127.0.0.1", "localhost");

    driver().open("/empty.html", withBrowserState(stateFile)
      .loggedInIf(isLoggedIn())
      .orLogin(() -> {
        login();
        driver().open(anotherOrigin + "/start_page.html"); // e.g. login finished on identity provider page
      }));

    assertThat(loginCount).hasValue(1);
    BrowserState saved = BrowserState.fromFile(stateFile);
    assertThat(saved.origin()).isEqualTo(getBaseUrl());
    assertThat(saved.localStorage()).containsEntry("token", "valid-token");
  }

  @Test
  void orLogin_logsInAgain_ifSavedStateIsNotValidJson() throws Exception {
    Files.createDirectories(stateFile.getParent());
    Files.writeString(stateFile, "{ broken json", UTF_8);

    driver().open("/empty.html", authState());

    assertThat(loginCount).hasValue(1);
    assertThat(isLoggedIn().getAsBoolean()).isTrue();
    assertThat(BrowserState.fromFile(stateFile).origin()).isEqualTo(getBaseUrl());
  }

  @Test
  void orLogin_failsIfUserIsNotLoggedInRightAfterLogin() {
    OpenOptions options = withBrowserState(stateFile)
      .loggedInIf(() -> false)
      .orLogin(this::login);
    SelenideDriver driver = driver();

    assertThatThrownBy(() -> driver.open("/empty.html", options))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("User is not logged in right after login (%s). Check the login code and the loggedInIf() check.", stateFile);
    assertThat(loginCount).hasValue(1);
  }

  private OpenOptions authState() {
    return withBrowserState(stateFile)
      .loggedInIf(isLoggedIn())
      .orLogin(this::login);
  }

  private BooleanSupplier isLoggedIn() {
    return () -> "valid-token".equals(driver().getLocalStorage().getItem("token"));
  }

  private void login() {
    loginCount.incrementAndGet();
    driver().open("/start_page.html");
    driver().getLocalStorage().setItem("token", "valid-token");
  }
}
