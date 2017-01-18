package demo.Presentation;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class PresentationDemo {

    public static void main(String[] args) throws InterruptedException {
        Configuration.PresentationMode.active = true;

        open("http://selenide.org");
        ElementsCollection collection = $$("div.wiki *:not(a):not(br):not(img)");
        for (SelenideElement e: collection){
            e.click();
        }

        for (SelenideElement e: collection){
            e.doubleClick();
        }

        Configuration.PresentationMode.flashElements = false;
        Configuration.PresentationMode.markElements = true;
        Configuration.PresentationMode.markColor = "#ff0000";

        collection.last().click();
        Thread.sleep(5000);
    }
}
