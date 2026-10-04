package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SharedDownloadsFolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.TestResources.toFile;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

final class FirefoxWithProfileTest extends BaseIntegrationTest {
  private FirefoxDriver firefox;
  private SelenideDriver customFirefox;

  @BeforeEach
  void setUp() {
    assumeThat(browser().isFirefox()).isTrue();

    FirefoxOptions options = new FirefoxOptions();
    if (browser().isHeadless()) options.addArguments("-headless");
    firefox = new FirefoxDriver(options);
  }

  @AfterEach
  void tearDown() {
    if (customFirefox != null) {
      customFirefox.close();
    }
    if (firefox != null) {
      firefox.quit();
    }
  }

  @Test
  void installsExtensionIntoFirefox() throws IOException {
    String extensionId = firefox.installExtension(zip("hello-world-extension"), true);
    assertThat(extensionId).isNotBlank();

    SelenideConfig config = new SelenideConfig().browser("firefox").baseUrl(getBaseUrl());
    customFirefox = new SelenideDriver(config, firefox, null, new SharedDownloadsFolder("build/downloads/456"));
    customFirefox.open("/page_with_selects_without_jquery.html");
    customFirefox.$("#non-clickable-element").shouldBe(visible);
    customFirefox.$("body").shouldHave(attribute("data-hello-world-extension", "installed"));
  }

  private Path zip(String resourceDir) throws IOException {
    File sourceDir = toFile(resourceDir);
    File xpi = File.createTempFile(sourceDir.getName(), ".xpi");
    xpi.deleteOnExit();
    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(xpi.toPath()))) {
      for (File file : requireNonNull(sourceDir.listFiles())) {
        zos.putNextEntry(new ZipEntry(file.getName()));
        Files.copy(file.toPath(), zos);
        zos.closeEntry();
      }
    }
    return xpi.toPath();
  }
}
