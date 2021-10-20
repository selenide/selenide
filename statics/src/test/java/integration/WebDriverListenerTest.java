package integration;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.WebDriverListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class WebDriverListenerTest extends IntegrationTest {
  private final DeprecatedListener deprecatedListener = new DeprecatedListener();
  private final Selenium4Listener listener = new Selenium4Listener();

  @BeforeEach
  void openTestPage() {
    Selenide.closeWebDriver();
  }

  @AfterEach
  void tearDown() {
    WebDriverRunner.removeListener(listener);
    WebDriverRunner.removeListener(deprecatedListener);
  }

  @Test
  void canAddEventListener() {
    WebDriverRunner.addListener(deprecatedListener);

    open("/page_with_selects_without_jquery.html");
    open("/page_with_frames.html");

    assertThat(deprecatedListener.befores).containsExactly(
      getBaseUrl() + "/page_with_selects_without_jquery.html",
      getBaseUrl() + "/page_with_frames.html"
    );
    assertThat(deprecatedListener.afters).containsExactly(
      getBaseUrl() + "/page_with_selects_without_jquery.html",
      getBaseUrl() + "/page_with_frames.html"
    );
  }

  @Test
  void canAddListener() {
    WebDriverRunner.addListener(listener);

    open("/page_with_selects_without_jquery.html");
    open("/page_with_frames.html");

    assertThat(listener.befores).containsExactly(
      getBaseUrl() + "/page_with_selects_without_jquery.html",
      getBaseUrl() + "/page_with_frames.html"
    );
    assertThat(listener.afters).containsExactly(
      getBaseUrl() + "/page_with_selects_without_jquery.html",
      getBaseUrl() + "/page_with_frames.html"
    );
  }

  @Test
  void canAddBothNewAndOldListeners() {
    WebDriverRunner.addListener(listener);
    WebDriverRunner.addListener(deprecatedListener);
    open("/page_with_frames.html");

    assertThat(deprecatedListener.befores).containsExactly(getBaseUrl() + "/page_with_frames.html");
    assertThat(deprecatedListener.afters).containsExactly(getBaseUrl() + "/page_with_frames.html");
    assertThat(listener.befores).containsExactly(getBaseUrl() + "/page_with_frames.html");
    assertThat(listener.afters).containsExactly(getBaseUrl() + "/page_with_frames.html");
  }

  public static class Selenium4Listener implements WebDriverListener {
    private final List<String> befores = new ArrayList<>();
    private final List<String> afters = new ArrayList<>();

    @Override
    public void beforeTo(WebDriver.Navigation navigation, String url) {
      befores.add(url);
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, String url) {
      afters.add(url);
    }

    @Override
    public void beforeTo(WebDriver.Navigation navigation, URL url) {
      befores.add(url.toString());
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, URL url) {
      afters.add(url.toString());
    }
  }

  public static class DeprecatedListener extends AbstractWebDriverEventListener {
    private final List<String> befores = new ArrayList<>();
    private final List<String> afters = new ArrayList<>();

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
      befores.add(url);
    }

    @Override
    public void afterNavigateTo(String url, WebDriver driver) {
      afters.add(url);
    }
  }
}
