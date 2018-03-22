package demo.hookactions;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.hookactions.HookActions;

import java.net.URL;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class HookActionsDemo {

  public static void main(String[] args) throws InterruptedException {
    URL url = HookActionsDemo.class.getResource("/page_with_big_divs.html");
    HookActions.getInstance().addBeforeAction("beforeDemo", new DemoAction("ing"));
    HookActions.getInstance().addAfterAction("afterDemo", new DemoAction("ed"));
    HookActions.getInstance().addErrorAction("errorDemo", new DemoAction(" failed"));

    open(url);
    ElementsCollection collection = $$("div");
    for (SelenideElement e : collection) {
      e.click();
    }

    for (SelenideElement e : collection) {
      e.doubleClick();
    }

    $("div.these.are.not.droid.what.you.looking.for").click();
  }
}
