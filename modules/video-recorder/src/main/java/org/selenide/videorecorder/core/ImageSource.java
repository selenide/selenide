package org.selenide.videorecorder.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.IOUtils.resourceToByteArray;

public interface ImageSource {
  BufferedImage getImage() throws IOException;
  void dispose();
}

record FileImageSource(File file) implements ImageSource {
  @Override
  public BufferedImage getImage() throws IOException {
    try (InputStream in = new FileInputStream(file)) {
      return ImageIO.read(in);
    }
  }

  @Override
  public void dispose() {
    if (!file.delete()) {
      file.deleteOnExit();
    }
  }

  @Override
  public String toString() {
    return file.toString();
  }
}

record ClasspathResource(String file) implements ImageSource {
  @Override
  public BufferedImage getImage() throws IOException {
    try (InputStream in = new ByteArrayInputStream(resourceToByteArray(file, null))) {
      return ImageIO.read(in);
    }
  }

  @Override
  public void dispose() {
  }

  @Override
  public String toString() {
    return "cp:" + file;
  }
}
