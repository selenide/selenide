package demo.presentation;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class PresentationDemo {

  public static void main(String[] args) throws InterruptedException {
    Configuration.presentationMode.active = true;

    open("http://selenide.org");
    ElementsCollection collection = $$("div.wiki *:not(a):not(br):not(img)");
    for (SelenideElement e : collection) {
      e.click();
    }

    for (SelenideElement e : collection) {
      e.doubleClick();
    }

    Configuration.presentationMode.flashElements = false;
    Configuration.presentationMode.markElements = true;
    Configuration.presentationMode.markColor = "#ff0000";

    collection.last().click();
    Thread.sleep(5000);
  }
}
