package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static java.lang.Thread.currentThread;

public class UploadFileFromClasspath implements Command<File> {
  UploadFile uploadFile = new UploadFile();
  
  @Override
  public File execute(SelenideElement proxy, WebElementSource inputField, Object[] args) throws IOException {
    String[] fileName = (String[]) args[0];
    File[] files = new File[fileName.length];
    for (int i = 0; i < fileName.length; i++) {
      files[i] = findFileInClasspath(fileName[i]);
    }

    return uploadFile.execute(proxy, inputField, files);
  }

  protected File findFileInClasspath(String name) {
    URL resource = currentThread().getContextClassLoader().getResource(name);
    if (resource == null) {
      throw new IllegalArgumentException("File not found in classpath: " + name);
    }
    try {
      return new File(resource.toURI());
    }
    catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
