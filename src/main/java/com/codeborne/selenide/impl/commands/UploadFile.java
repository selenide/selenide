package com.codeborne.selenide.impl.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.Describe;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class UploadFile implements Command<File> {
  @Override
  public File execute(SelenideElement proxy, WebElementSource locator, Object[] args) throws IOException {
    File[] file;
    if (args instanceof File[]) {
      file = (File[]) args;
    }
    else {
      file = (File[]) args[0];
    }
    if (file.length == 0) {
      throw new IllegalArgumentException("No files to upload");
    }

    WebElement inputField = locator.getWebElement();
    File uploadedFile = uploadFile(inputField, file[0]);

    if (file.length > 1) {
      SelenideElement form = proxy.closest("form");
      for (int i = 1; i < file.length; i++) {
        uploadFile(cloneInputField(form, inputField), file[i]);
      }
    }

    return uploadedFile;

  }

  protected File uploadFile(WebElement inputField, File file) throws IOException {
    if (!"input".equalsIgnoreCase(inputField.getTagName())) {
      throw new IllegalArgumentException("Cannot upload file because " + Describe.describe(inputField) + " is not an INPUT");
    }

    if (!file.exists()) {
      throw new IllegalArgumentException("File not found: " + file.getAbsolutePath());
    }

    String canonicalPath = file.getCanonicalPath();
    inputField.sendKeys(canonicalPath);
    return new File(canonicalPath);
  }

  protected WebElement cloneInputField(SelenideElement form, WebElement inputField) {
    return executeJavaScript(
        "var fileInput = document.createElement('input');" +
            "fileInput.setAttribute('type', arguments[1].getAttribute('type'));" +
            "fileInput.setAttribute('name', arguments[1].getAttribute('name'));" +
            "fileInput.style.width = '1px';" +
            "fileInput.style.height = '1px';" +
            "arguments[0].appendChild(fileInput);" +
            "return fileInput;",
        form, inputField);
  }
}
