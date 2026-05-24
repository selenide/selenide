package org.selenide.grid;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GridDownloadsFolderTest {
  private final Driver driver = mock();

  @BeforeEach
  void setUp() {
    RemoteWebDriver webDriver = mock();
    when(driver.getWebDriver()).thenReturn(webDriver);
    when(webDriver.getSessionId()).thenReturn(new SessionId("grid-session-123"));
  }

  @Test
  void toStringContainsSessionId() {
    GridDownloadsFolder folder = new GridDownloadsFolder(driver);
    assertThat(folder.toString()).isEqualTo("GridDownloadsFolder{grid-session-123}");
  }
}
