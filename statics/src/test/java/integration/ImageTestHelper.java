package integration;

import org.assertj.core.util.Sets;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTestHelper {
  static final Set<Color> RED = Sets.set(
    new Color(234, 51, 35),
    new Color(235, 51, 35),
    new Color(233, 54, 32),
    new Color(255, 0, 0)
  );
  static final Set<Color> BLUE = Sets.set(
    new Color(115, 251, 253),
    new Color(115, 252, 253),
    new Color(117, 251, 253),
    new Color(121, 251, 254),
    new Color(0, 255, 255)
  );
  static final Set<Color> GRAY = Sets.set(new Color(136, 136, 136));
  static final Set<Color> PINK = Sets.set(new Color(136, 136, 136), new Color(255, 192, 203));

  private static final int CORNER_INSET = 1;
  // Element screenshots can be shifted down by ~1 CSS pixel (= 2 physical px at 2x DPR),
  // leaving the top row(s) of the captured image as whitespace instead of the red border.
  private static final int TOP_INSET = 2;

  static void assertBorder(BufferedImage img, Set<Color> color) {
    assertBorder(img, color, img.getWidth());
  }

  static void assertBorder(BufferedImage img, Set<Color> color, int width) {
    assertTopBorder(img, color, width);
    assertBottomBorder(img, color, width);
    assertLeftBorder(img, color);
    assertRightBorder(img, color);
  }

  static void assertTopBorder(BufferedImage img, Set<Color> color, int width) {
    for (int x = CORNER_INSET; x < width - CORNER_INSET; x++) {
      assertColor(img, "top border @" + x, x, TOP_INSET, color);
    }
  }

  static void assertBottomBorder(BufferedImage img, Set<Color> color, int width) {
    for (int x = CORNER_INSET; x < width - CORNER_INSET; x++) {
      assertColor(img, "bottom border @" + x, x, img.getHeight() - 1 - CORNER_INSET, color);
    }
  }

  static void assertLeftBorder(BufferedImage img, Set<Color> color) {
    for (int y = TOP_INSET; y < img.getHeight() - CORNER_INSET; y++) {
      assertColor(img, "left border @" + y, CORNER_INSET, y, color);
    }
  }

  static void assertRightBorder(BufferedImage img, Set<Color> color) {
    for (int y = TOP_INSET; y < img.getHeight() - CORNER_INSET; y++) {
      assertColor(img, "right border @" + y, img.getWidth() - 1 - CORNER_INSET, y, color);
    }
  }

  static void assertBody(BufferedImage img, Set<Color> color) {
    assertBody(img, color, img.getWidth());
  }

  static void assertBody(BufferedImage img, Set<Color> color, int width) {
    assertTopBody(img, color, width);
    assertBottomBody(img, color, width);
    assertLeftBody(img, color);
    assertRightBody(img, color, width);
  }

  static void assertTopBody(BufferedImage img, Set<Color> color, int width) {
    for (int x = 11; x < width - 11; x++) {
      assertColor(img, "top body @" + x, x, 11, color);
    }
  }

  static void assertBottomBody(BufferedImage img, Set<Color> color, int width) {
    for (int x = 11; x < width - 11; x++) {
      assertColor(img, "bottom body @" + x, x, img.getHeight() - 11, color);
    }
  }

  static void assertLeftBody(BufferedImage img, Set<Color> color) {
    for (int y = 11; y < img.getHeight() - 11; y++) {
      assertColor(img, "left body @" + y, 11, y, color);
    }
  }

  static void assertRightBody(BufferedImage img, Set<Color> color, int width) {
    for (int y = 11; y < img.getHeight() - 11; y++) {
      assertColor(img, "right body @" + y, width - 11, y, color);
    }
  }

  private static void assertColor(BufferedImage img, String description, int x, int y, Set<Color> expectedColor) {
    assertThat(new Color(img.getRGB(x, y))).as(description).isIn(expectedColor);
  }
}
