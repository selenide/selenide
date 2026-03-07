package org.selenide.videorecorder.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.assertj.core.api.Assertions.assertThat;

class ImagesTest {
  private Path dir;

  @BeforeEach
  void setUp() throws IOException {
    dir = Files.createTempDirectory("selenide-test-");
    dir.toFile().deleteOnExit();
  }

  @Test
  void findsBiggestWidthAndBiggestHeight() throws IOException {
    createImage("image-1.png", 200, 100);
    createImage("image-2.png", 300, 110);
    createImage("image-3.png", 10, 220);

    assertThat(Images.findMaxSize(dir, "image", "png")).isEqualTo(new Dimension(300, 220));
  }

  private void createImage(String name, int width, int height) throws IOException {
    BufferedImage img = new BufferedImage(width, height, TYPE_INT_RGB);
    assertThat(ImageIO.write(img, "png", new File(dir.toFile(), name))).isTrue();
  }
}
