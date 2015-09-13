package demo.zoom;

import java.net.URL;

import static com.codeborne.selenide.Selenide.*;

public class ZoomDemo {
  static double zoom = 1.0;
  
  public static void main(String[] args) {
    URL url = ZoomDemo.class.getResource("/page_with_dynamic_select.html");
    open(url);
    
    zoomIn(2.0);
    zoomOut(0.1);
    zoomIn(2.0);
    zoomOut(0.1);
    zoomIn(1.0);

    sleep(1000);
  }

  private static void zoomIn(double upTo) {
    while (zoom < upTo) {
      zoom += 0.1;
      zoom(zoom);
      sleep(20);
    }
  }

  private static void zoomOut(double upTo) {
    while (zoom > upTo) {
      zoom -= 0.1;
      zoom(zoom);
      sleep(20);
    }
  }
}
