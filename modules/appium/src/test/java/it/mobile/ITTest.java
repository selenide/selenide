package it.mobile;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.TextReportExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TextReportExtension.class)
public abstract class ITTest {
  @BeforeEach
  final void resetSettings() {
    Configuration.timeout = 10_000;
    Configuration.pageLoadTimeout = -1;
  }
}
