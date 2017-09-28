package demo.presentation;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.net.URL;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class PresentationDemo {

  public static void main(String[] args) throws InterruptedException {
    Configuration.presentationMode.active = true;
    URL url = PresentationDemo.class.getResource("/page_for_presentation_mode.html");
    open(url);
    ElementsCollection collection = $$("div.rect");
    for (SelenideElement e : collection) {
      e.click();
    }

    for (SelenideElement e : collection) {
      e.doubleClick();
    }

    Configuration.presentationMode.flashElements = false;
    Configuration.presentationMode.markElements = true;
    Configuration.presentationMode.markColor = "#ff0000";

    for (SelenideElement e : collection) {
      e.click();
    }
    Thread.sleep(5000);
  }
}
