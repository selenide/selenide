package com.codeborne.selenide.appium;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.CollectionSource;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.bidi.BiDiException;
import org.openqa.selenium.remote.DesiredCapabilities;

class SelenideAppiumCollectionSubclassTest {

  @BeforeEach
  void setUp() {
    AndroidDriver androidDriver = mock();
    when(androidDriver.getCapabilities()).thenReturn(new DesiredCapabilities());
    when(androidDriver.getBiDi()).thenThrow(new BiDiException("Not working in Android"));
    WebDriverRunner.setWebDriver(androidDriver);
  }

  @AfterEach
  void tearDown() {
    Selenide.closeWebDriver();
  }

  @Test
  void subclass_can_produce_elements_of_its_own_class() {
    MyCollection collection = myCollection();

    assertThat(collection.get(0)).isInstanceOf(MyElement.class);
    assertThat(collection.first()).isInstanceOf(MyElement.class);
    assertThat(collection.last()).isInstanceOf(MyElement.class);
  }

  @Test
  void derived_collections_keep_the_subclass() {
    MyCollection collection = myCollection();

    assertThat(collection.filter(Condition.visible)).isInstanceOf(MyCollection.class);
    assertThat(collection.first(2)).isInstanceOf(MyCollection.class);
    assertThat(collection.snapshot()).isInstanceOf(MyCollection.class);
  }

  private static MyCollection myCollection() {
    return new MyCollection(new BySelectorCollection(WebDriverRunner.driver(), By.className("android.widget.TextView")));
  }

  private interface MyElement extends SelenideAppiumElement {
  }

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
  }
}
