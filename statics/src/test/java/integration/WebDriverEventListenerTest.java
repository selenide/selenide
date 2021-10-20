package integration;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
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

public class WebDriverEventListenerTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    Selenide.closeWebDriver();
  }

  @Test
  void canAddEventListener() {
    DeprecatedListener listener = new DeprecatedListener();
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
  void canAddListener() {
    Selenium4Listener listener = new Selenium4Listener();
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
    Selenium4Listener newListener = new Selenium4Listener();
    WebDriverRunner.addListener(newListener);
    DeprecatedListener oldListener = new DeprecatedListener();
    WebDriverRunner.addListener(oldListener);

    open("/page_with_frames.html");

    assertThat(oldListener.befores).containsExactly(getBaseUrl() + "/page_with_frames.html");
    assertThat(oldListener.afters).containsExactly(getBaseUrl() + "/page_with_frames.html");
    assertThat(newListener.befores).containsExactly(getBaseUrl() + "/page_with_frames.html");
    assertThat(newListener.afters).containsExactly(getBaseUrl() + "/page_with_frames.html");
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
