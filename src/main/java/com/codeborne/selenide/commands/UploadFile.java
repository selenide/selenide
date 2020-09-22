package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.impl.ElementDescriber;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.stream.Collectors.joining;

@ParametersAreNonnullByDefault
public class UploadFile implements Command<File> {
  private final ElementDescriber describe = inject(ElementDescriber.class);

  @Override
  @CheckReturnValue
  @Nonnull
  public File execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) throws IOException {
    File[] file = getFiles(args);
    checkFilesGiven(file);
    checkFilesExist(file);

    WebElement inputField = locator.getWebElement();
    Driver driver = locator.driver();
    checkValidInputField(driver, inputField);

    String fileNames = Stream.of(file).map(this::canonicalPath).collect(joining("\n"));
    uploadFiles(driver.config(), inputField, fileNames);
    return file[0].getCanonicalFile();
  }

  private void checkFilesGiven(File[] file) {
    if (file.length == 0) {
      throw new IllegalArgumentException("No files to upload");
    }
  }

  private void checkFilesExist(File[] file) {
    for (File f : file) {
      if (!f.exists()) {
        throw new IllegalArgumentException("File not found: " + f.getAbsolutePath());
      }
    }
  }

  @SuppressWarnings("SuspiciousArrayCast")
  @CheckReturnValue
  @Nonnull
  private File[] getFiles(@Nullable Object[] args) {
    return args instanceof File[] ? (File[]) args : firstOf(args);
  }

  private String canonicalPath(File file) {
    try {
      return file.getCanonicalPath();
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot get canonical path of file " + file, e);
    }
  }

  protected void uploadFiles(Config config, WebElement inputField, String fileNames) {
    Stopwatch stopwatch = new Stopwatch(config.timeout());
    do {
      try {
        inputField.sendKeys(fileNames);
        break;
      }
      catch (ElementNotInteractableException notInteractable) {
        if (stopwatch.isTimeoutReached()) {
          throw notInteractable;
        }
        stopwatch.sleep(config.pollingInterval());
      }
    } while (!stopwatch.isTimeoutReached());
  }

  private void checkValidInputField(Driver driver, WebElement inputField) {
    if (!"input".equalsIgnoreCase(inputField.getTagName())) {
      throw new IllegalArgumentException("Cannot upload file because " +
        describe.briefly(driver, inputField) + " is not an INPUT");
    }
  }
}
