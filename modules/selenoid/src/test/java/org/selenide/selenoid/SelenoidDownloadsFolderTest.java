package org.selenide.selenoid;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SelenoidDownloadsFolderTest {
  private final Driver driver = new DriverStub(new SelenideConfig().remote("http://example.com:4444/wd/hub"));

  @Test
  void toStringContainsSessionId() {
    SelenoidDownloadsFolder folder = new SelenoidDownloadsFolder(driver);
    assertThat(folder.toString()).isEqualTo("SelenoidDownloadsFolder{testSession}");
  }
}
