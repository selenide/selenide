package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.codeborne.selenide.commands.Util.firstOf;
import static java.lang.Thread.currentThread;
import static org.apache.commons.io.FileUtils.copyURLToFile;
import static org.apache.commons.io.FilenameUtils.getName;

public class UploadFileFromClasspath implements Command<File> {
  private final UploadFile uploadFile = new UploadFile();

  @Override
  public File execute(SelenideElement proxy, WebElementSource inputField, Object @Nullable [] args) {
    String[] fileName = firstOf(args);
    int fileCount = fileName.length;
    File[] files = new File[fileCount];
    for (int i = 0; i < fileCount; i++) {
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
      URI uri = resource.toURI();
      return uri.isOpaque() ? saveAsTemporaryFile(resource) : new File(uri);
    }
    catch (URISyntaxException | IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private File saveAsTemporaryFile(URL classpathResource) throws IOException {
    Path directory = Files.createTempDirectory("selenide-upload-");
    File fileToUpload = new File(directory.toFile().getAbsoluteFile(), getName(classpathResource.getFile()));
    copyURLToFile(classpathResource, fileToUpload);
    return fileToUpload;
  }
}
