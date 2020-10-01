package com.codeborne.selenide.appium;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.FileHelper;
import com.codeborne.selenide.impl.PageSourceExtractor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public class AppiumScreenSourceExtractor implements PageSourceExtractor {
  private static final Logger log = LoggerFactory.getLogger(AppiumScreenSourceExtractor.class);

  @Nonnull
  @CheckReturnValue
  @Override
  public File extract(Config config, WebDriver driver, String fileName) {
    File pageSource = new File(config.reportsFolder(), fileName + ".xml");
    try {
      writeToFile(driver.getPageSource(), pageSource);
    }
    catch (WebDriverException e) {
      log.warn("Failed to save page source to {}", fileName, e);
      writeToFile(e.toString(), pageSource);
      return pageSource;
    }
    catch (RuntimeException e) {
      log.error("Failed to save page source to {}", fileName, e);
      writeToFile(e.toString(), pageSource);
    }
    return pageSource;
  }

  protected void writeToFile(String content, File targetFile) {
    try (ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(UTF_8))) {
      FileHelper.copyFile(in, targetFile);
    }
    catch (IOException e) {
      log.error("Failed to write file {}", targetFile.getAbsolutePath(), e);
    }
  }
}
