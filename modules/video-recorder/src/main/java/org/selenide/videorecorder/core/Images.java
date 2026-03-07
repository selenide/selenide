package org.selenide.videorecorder.core;

import org.openqa.selenium.Dimension;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

class Images {
  /**
   * Find the maximum width and the maximum height among all images in the given directory
   */
  static Dimension findMaxSize(Path dir, String prefix, String suffix) {
    int maxWidth = 0;
    int maxHeight = 0;

    try (Stream<Path> paths = Files.walk(dir, 2)) {
      for (Path path : (Iterable<Path>) () -> paths.iterator()) {
        String fileName = path.toFile().getName();
        if (!fileName.startsWith(prefix) || !fileName.endsWith(suffix)) continue;

        try (ImageInputStream iis = ImageIO.createImageInputStream(path.toFile())) {
          Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
          if (!readers.hasNext()) continue;

          ImageReader reader = readers.next();
          try {
            reader.setInput(iis, true, true);
            maxWidth = Math.max(maxWidth, reader.getWidth(0));
            maxHeight = Math.max(maxHeight, reader.getHeight(0));
          }
          finally {
            reader.dispose();
          }
        }
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to read image sizes from directory " + dir.toAbsolutePath(), e);
    }

    return new Dimension(maxWidth, maxHeight);
  }
}
