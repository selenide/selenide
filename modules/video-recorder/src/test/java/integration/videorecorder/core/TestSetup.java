package integration.videorecorder.core;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestSetup implements BeforeAllCallback {
  @Override
  public void beforeAll(ExtensionContext context) {
    Configuration.webdriverLogsEnabled = true;
  }
}
