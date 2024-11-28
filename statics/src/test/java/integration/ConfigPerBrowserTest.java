package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.config;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.inNewBrowser;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.cookie;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConfigPerBrowserTest extends IntegrationTest {
  private static final SelenideElement h1 = $("h1");

  @BeforeEach
  @AfterEach
  void setUp() {
    closeWebDriver();
  }

  @Test
  void canOpenBrowserWithSpecificSettings() {
    open("/page_with_images.html");
    h1.shouldHave(text("Images"));
    assertSizeGreaterThan(500, 400);
    WebDriver webDriver = getWebDriver();

    open("/page_with_uploads.html", config().browserSize("500x400"));
    h1.shouldHave(text("File uploads"));
    assertSize(500, 400);

    assertThatThrownBy(webDriver::getTitle)
      .as("The first webdriver should be already closed as a result of `open(url, config)`")
      .isInstanceOf(NoSuchSessionException.class);
  }

  @Test
  public void inNewBrowser_withCustomConfig() {
    SelenideConfig originalConfig = new SelenideConfig().baseUrl(getBaseUrl());
    open("/page_with_images.html", originalConfig);
    h1.shouldHave(text("Images"));
    assertSizeGreaterThan(500, 400);

    getWebDriver().manage().addCookie(new Cookie("bober", "kurwa"));
    webdriver().shouldHave(cookie("bober", "kurwa"));

    SelenideConfig anotherConfig = new SelenideConfig().baseUrl(getBaseUrl()).browserSize("500x400");
    inNewBrowser(anotherConfig, () -> {
      open("/page_with_uploads.html");
      h1.shouldHave(text("File uploads"));
      assertSize(500, 400);
      webdriver().shouldNotHave(cookie("bober", "kurwa"));
    });

    h1.shouldHave(text("Images"));
    open("/page_with_images.html", originalConfig);
    webdriver().shouldHave(cookie("bober", "kurwa"));
    assertSizeGreaterThan(500, 400);
  }

  @Test
  public void inNewBrowser_withCustomConfigInside() {
    SelenideConfig originalConfig = new SelenideConfig().baseUrl(getBaseUrl());
    open("/page_with_images.html", originalConfig);
    h1.shouldHave(text("Images"));

    inNewBrowser(() -> {
      SelenideConfig anotherConfig = new SelenideConfig().baseUrl(getBaseUrl());
      open("/page_with_uploads.html", anotherConfig);
      h1.shouldHave(text("File uploads"));
    });

    h1.shouldHave(text("Images"));
    open("/page_with_images.html", originalConfig);
    h1.shouldHave(text("Images"));
  }

  private void assertSize(int width, int height) {
    Dimension size = getWebDriver().manage().window().getSize();
    assertThat(size).isEqualTo(new Dimension(width, height));
  }

  private void assertSizeGreaterThan(int width, int height) {
    Dimension size = getWebDriver().manage().window().getSize();
    assertThat(size.getWidth()).isGreaterThan(width);
    assertThat(size.getHeight()).isGreaterThan(height);
  }
}
