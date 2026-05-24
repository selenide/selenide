package org.selenide.moon;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoonDownloadsFolderTest {
  private final Driver driver = new DriverStub(new SelenideConfig().remote("http://example.com:4444/wd/hub"));

  @Test
  void toStringContainsSessionId() {
    MoonDownloadsFolder folder = new MoonDownloadsFolder(driver);
    assertThat(folder.toString()).isEqualTo("MoonDownloadsFolder{testSession}");
  }
}
