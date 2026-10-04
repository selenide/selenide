package it.mobile.android;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppiumCollection;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import com.codeborne.selenide.commands.Commands;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.driver;
import static org.assertj.core.api.Assertions.assertThat;

class CustomAppiumCollectionTest extends BaseApiDemosTest {

  @BeforeEach
  void setUp() {
    Commands.getInstance().add("myAppiumText", new MyAppiumText());
  }

  @Test
  void collectionOfCustomSelenideAppiumElements() {
    $(By.xpath(".//*[@text='Text']")).click();
    MyCollection elements = $$$(By.xpath("//android.widget.TextView"));
    elements.shouldHave(sizeGreaterThan(3));

    elements.third().shouldHave(text("Linkify"));
    assertThat(elements.third().myAppiumText()).isEqualTo("Linkify");
  }

  private static MyCollection $$$(By selector) {
    return new MyCollection(new BySelectorCollection(driver(), selector));
  }

  @NullMarked
  private interface MyElement extends SelenideAppiumElement {
    String myAppiumText();
  }

  @NullMarked
  private static class MyCollection extends SelenideAppiumCollection {
    private MyCollection(CollectionSource collection) {
      super(collection, MyElement.class);
    }

    @Override
    protected MyCollection create(CollectionSource source) {
      return new MyCollection(source);
    }

    @Override
    public MyElement get(int index) {
      return (MyElement) super.get(index);
    }

    @Override
    public MyElement first() {
      return (MyElement) super.first();
    }

    @Override
    public MyElement last() {
      return (MyElement) super.last();
    }

    public MyElement third() {
      return get(2);
    }
  }

  @NullMarked
  private static class MyAppiumText implements Command<String> {
    @Override
    public String execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
      return locator.getWebElement().getText();
    }
  }
}
